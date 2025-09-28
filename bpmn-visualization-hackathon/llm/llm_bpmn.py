from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import ChatPromptTemplate
from utils import extract_error_message
from langchain_ollama import ChatOllama
import json
import os
from pydantic import  ValidationError
from typing import Optional
from entities import BpmnDiagramSchema
from dotenv import load_dotenv

load_dotenv()
OLLAMA_CONTAINER_NAME = os.getenv("OLLAMA_CONTAINER_NAME")

MODEL_NAME = os.getenv("OLLAMA_MODEL_NAME")
chat_model = ChatOllama(
    model=MODEL_NAME,
    temperature=0.5,
    num_ctx=4096,
    base_url=f"http://{OLLAMA_CONTAINER_NAME}:11434"
)

SYSTEM_PROMPT = """Ты — эксперт-аналитик бизнес-процессов, специализирующийся на нотации BPMN 2.0.
Твоя задача — внимательно проанализировать описание бизнес-процесса, предоставленное пользователем, и преобразовать его в структурированный JSON-объект, точно соответствующий ЗАДАННОЙ СХЕМЕ.
Для начала надо провести анализ
1 **Анализ процесса:**
    *   Внимательно прочитай описание процесса.
    *   Определи основных участников (роли, системы, отделы). Каждый уникальный участник должен стать отдельной "дорожкой" (`lane`). Все дорожки должны находиться внутри "пула" (`pool`).
    *   Выяви последовательность действий (задачи, шаги).
    *   Определи точки принятия решений (ветвления) и действия после ветвлений.
    *   Критически важно: Определи начальные и конечные точки процесса.
    *   Определи тип каждой задачи (выполняется человеком - `userTask`, системой - `serviceTask`, скриптом - `scriptTask`, или общая - `task`).
    *   Определи тип шлюзов (эксклюзивный выбор - `exclusiveGateway`, параллельное выполнение - `parallelGateway`, и т.д.).
    *   Выяви связи (потоки управления) между элементами.
    *   Критически важно: Процесс обязательно должен иметь startEvent и endEvent
    *   у ветвлений (шлюзов) обязательна должно быть в массиве targetRef 2 элемента
    *   для всех элементов, кроме startEvent должен быть элемент у который на него ссылатся через targetRef
    Опиши процесс в обычном текстовом формате, где можно будет выявить самое название процесса(pool), участников (lane) начало события (startEvent), задачи (`userTask`, `serviceTask`, `scriptTask`, `task`), ветвления( шлюзы, `exclusiveGateway`, `parallelGateway` ) и конец процесса endEvent.
    Процесс заканчиватся элементом с типом endEvent
    Обязтельно проследи, чтобы процесс был логичным и последовательным, нужно чтобы каждый элемент имел связь и смог доходить до endEvent

Твоя цель: Предоставить ПОЛНЫЙ и КОРРЕКТНЫЙ JSON на основе запроса пользователя, строго следуя описанной схеме. Если в описании пользователя есть неясности, сделай наиболее логичное предположение для построения связного процесса
"Answer the user query. Wrap the output in `json` tags\n{format_instructions}
"""

print("--- Использование с LCEL ---")


output_parser= PydanticOutputParser(pydantic_object=BpmnDiagramSchema)
prompt_template = ChatPromptTemplate.from_messages([
    ("system", SYSTEM_PROMPT),
    ("user", "{user_input}"), 
]).partial(format_instructions=output_parser.get_format_instructions())

chain = prompt_template | chat_model | output_parser



def generate_and_validate_bpmn(
    text_description: str,
    max_attempts: int = 10
) -> Optional[BpmnDiagramSchema]:
    print(f"--- Starting BPMN Generation for: ---\n{text_description}\n" + "-"*40)

    correction_prompt_template = ChatPromptTemplate.from_messages([
      ("system", "You are an expert BPMN diagram generator tasked with correcting a previous attempt. The previous JSON was invalid according to the Pydantic schema or business logic rules. The required schema is: {format_instructions}. Output ONLY the corrected, complete JSON object."),
      ("user", "The previous attempt to generate BPMN JSON for the text:\n```\n{original_text}\n```\nfailed with the following error(s):\n```\n{error_message}\n```\nThe invalid JSON generated was:\n```json\n{invalid_json}\n```\n Изучи тему ещё раз\n```\n{original_text}\n```\n и исправь ошибки в JSON"), 
  ]).partial(format_instructions=output_parser.get_format_instructions())

    correction_chain = correction_prompt_template | chat_model | output_parser

    current_attempt = 0
    last_error = None
    current_json_str = "{}"

    while current_attempt < max_attempts:
        current_attempt += 1
        print(f"\n--- Attempt {current_attempt}/{max_attempts} ---")

        try:
            if current_attempt == 1:
                print("Generating initial BPMN...")
                generated_object: BpmnDiagramSchema = chain.invoke({"user_input": text_description})
                print("answer: ", generated_object)
                current_json_str = generated_object.model_dump()
                print("Initial generation successful, performing logic validation...")
                print("✅ Logic validation passed!")
                print(json.dumps(current_json_str["pools"]))
                return current_json_str
            else:
                print(f"Attempting correction based on error:\n{extract_error_message(last_error)}")
                corrected_object: BpmnDiagramSchema = correction_chain.invoke({
                    "original_text": text_description,
                    "error_message": extract_error_message(last_error),
                    "invalid_json": current_json_str
                })
                current_json_str = corrected_object.model_dump()
                print("Correction attempt successful, performing logic validation...")
                print("✅ Logic validation passed after correction!")
                print(json.dumps(current_json_str["pools"]))
                return current_json_str
        except ValidationError as e:
            print(f"❌ Validation Error (Attempt {current_attempt}):")
            last_error = str(e)
            print(last_error)
        except Exception as e:
            print(f"❌ An unexpected error occurred (Attempt {current_attempt}):")
            last_error = f"LLM Call or Parsing Failed: {str(e)}"
        if current_attempt >= max_attempts:
             print(f"\n--- Failed to generate valid BPMN after {max_attempts} attempts ---")
             print(f"Last encountered error:\n{last_error}")
             return None


def get_bpmn_json(user_query):
    return generate_and_validate_bpmn(user_query)


from pydantic import BaseModel, Field, ConfigDict,  ValidationError, model_validator
from typing import List, Literal, Optional

IdentifierStr = Field()
NameStr = Field()

ElementTypeEnum = Literal[
    "startEvent",
    "intermediateThrowEvent",
    "intermediateCatchEvent",
    "endEvent",
    "task",
    "userTask",
    "serviceTask",
    "scriptTask",
    "exclusiveGateway",
    "parallelGateway",
    "inclusiveGateway",
    "eventBasedGateway",
    "dataObject",
    "textAnnotation",
]

class TargetRef(BaseModel):
    """Reference to a target element in a sequence flow."""
    model_config = ConfigDict(extra='forbid')

    ref: str = Field(default=None)
    name: Optional[str] = Field(default=None, description="Настраивается только Если у элемента type = exclusiveGateway или parallelGateway, Название должно отображать решение перехода или отвечать на вопрос из названия exclusiveGateway или parallelGateway. Например: Да, Нет, Больше, меньше")



class Element(BaseModel):
    """Represents a single element within a BPMN lane (task, event, gateway, etc.).
    Основные и самые важные правиал:
    * Критически важно: Никто не может ссылать на элемент с типом startEvent
    * В процессе обязательно должен быть хотя бы один элемент с типом startEvent
    * у exclusiveGateway и parallelGateway должно быть 2 элемента в массиве targetRef и ссылаться они должны на разные элементы
    * Критически важно, чтобы был хотя бы один элемент с типом endEvent
    * У endEvent массив targetRef пустой
    * Все элементы должны быть связаны, то есть иметь targetRef. Все, кроме endEvent
    """
    model_config = ConfigDict(extra='forbid')

    id: str = IdentifierStr
    type: ElementTypeEnum
    name: str = Field(default="",description="Настраивается только Если type = exclusiveGateway или parallelGateway, Название должно отображать решение перехода Например")
    targetRef: Optional[List[TargetRef]] = Field(default=None,description="Указывает на id следующей задачи, может указывать на задачи из другого лейна, Если type = exclusiveGateway или parallelGateway, то должно быть 2 элемента в массиве. Для элемента с type = endEvent массив долженр быть пустым")

    @model_validator(mode='after')
    def check_bpmn_logic(self) -> 'Element':
        errors = []
        if self.type == "exclusiveGateway" or self.type == "inclusiveGateway":
            if self.targetRef and self.targetRef and len(self.targetRef)<2:
                errors.append(f"У {self.type} c id {self.id} количество targetRef меньше 2, должно быть хотя бы 2")
        if self.type == 'endEvent' and self.targetRef and len(self.targetRef)>0:
             errors.append(f"Элемент endEvent c id {self.id} имеет количество targetRef, хотя должно быть 0")
        if errors:
            raise ValueError("BPMN Logic Validation Failed:\n- " + "\n- ".join(errors))
        return self


class Lane(BaseModel):
    """Represents a lane within a BPMN pool."""
    model_config = ConfigDict(extra='forbid')

    id: str = IdentifierStr
    name: str = NameStr
    elements: List[Element] = Field(default_factory=list, description="Элемент процесса. Который может выполнять сущность lane")


class Pool(BaseModel):
    """Represents a pool in a BPMN diagram, containing one or more lanes."""
    model_config = ConfigDict(extra='forbid')
    id: str = IdentifierStr
    lanes: List[Lane] = Field(min_length=1, description="Дорожка в BPMN диаграмме. Описывает сущность, которая выполняет действия в процессе")
    name: Optional[str] = None

class BpmnDiagramSchema(BaseModel):
    """
    Schema for describing BPMN diagrams with pools and lanes.
    Based on the provided JSON schema.
    """
    model_config = ConfigDict(extra='forbid')
    pools: List[Pool] = Field(min_length=1, description="Описывает процесс, который содержит сущности (lane)")

    @model_validator(mode='after')
    def check_bpmn_logic(self) -> 'BpmnDiagramSchema':
        all_element_ids = set()
        errors = []
        for pool in self.pools:
            for lane in pool.lanes:
                if not lane.name:
                        errors.append(f"Нет имени для лэйна с id = {lane.id}, Нужно ещё раз проанализировать бизнес процесс и задать корректное имя")
                for element in lane.elements:
                    if not element.name:
                        errors.append(f"Нет имени для элемента с id = {element.id}, Нужно ещё раз проанализировать бизнес процесс и задать корректное имя")
                    
                    if element.id in all_element_ids:
                        errors.append(f"Duplicate element ID found: {element.id}")
                    if element.type == "exclusiveGateway" or element.type == "inclusiveGateway":
                        if element.targetRef and element.targetRef and len(element.targetRef)<2:
                            errors.append(f"У {element.type} c id {element.id} количество targetRef меньше 2, должно быть хотя бы 2")
                    
                    all_element_ids.add(element.id)
                    is_start = element.type.lower().startswith('startevent')
                    is_end = element.type.lower().endswith('endevent')

                    if is_end and element.targetRef and len(element.targetRef) > 0:
                        errors.append(f"EndEvent '{element.id}' should not have outgoing flows (targetRef).")

                    has_elements_after_start = any(e for p in self.pools for l in p.lanes for e in l.elements if not e.type.lower().startswith('startevent'))
                    if is_start and (not element.targetRef or len(element.targetRef) == 0) and has_elements_after_start:
                         errors.append(f"StartEvent '{element.id}' должен содержать по крайней мере один исходящий поток (targetRef), если процесс продолжается. Возможно стоит какой-нибудь элемент изменить на startEvent")

        for pool in self.pools:
            for lane in pool.lanes:
                for element in lane.elements:
                    if element.targetRef:
                        for target in element.targetRef:
                            if target.ref and target.ref not in all_element_ids:
                                errors.append(f"Element '{element.id}' references non-existent target ID: {target.ref}")

        if errors:
            raise ValueError("BPMN Logic Validation Failed:\n- " + "\n- ".join(errors))

        return self

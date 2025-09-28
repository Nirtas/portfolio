import uuid
import re

def remove_json_backticks(input_str):
    input_str = input_str.strip()    
    if input_str.startswith('```json'):
        input_str = input_str[7:]
    if input_str.endswith('```'):
        input_str = input_str[:-3]
    return input_str.strip()



def transform_bpmn_v2(input_data):
    if not input_data:
        print("Error: Input 'pools' data is empty.")
        return None

    output_elements = []
    output_connections = []
    connection_counter = 1
    main_pool_data = input_data[0]
    diagram_id = main_pool_data.get("id", f"Diagram_{uuid.uuid4()}")
    diagram_name = main_pool_data.get("name", "Untitled BPMN Diagram")
    for pool_data in input_data:
        pool_id = pool_data["id"]
        output_elements.append({
            "id": pool_id,
            "type": "pool",
            "name": pool_data.get("name") if pool_data.get("name") else "" ,
            "parentId": None
        })

        for lane_data in pool_data.get("lanes", []):
            lane_id = lane_data["id"]
            output_elements.append({
                "id": lane_id,
                "type": "lane",
                "name": lane_data.get("name") if lane_data.get("name") else "",
                "parentId": pool_id
            })

            for element_data in lane_data.get("elements", []):
                element_id = element_data["id"]
                output_elements.append({
                    "id": element_id,
                    "type": element_data["type"],
                    "name": element_data.get("name") if element_data.get("name") else "",
                    "parentId": lane_id 
                })

                if "targetRef" in element_data and element_data["targetRef"]:
                    source_ref = element_id
                    for target_ref_obj in element_data["targetRef"]:
                        if "ref" not in target_ref_obj:
                            print(f"Skipping targetRef in element '{element_id}' due to missing 'ref': {target_ref_obj}")
                            continue

                        target_id = target_ref_obj["ref"]
                        connection_name = target_ref_obj.get("name")
                        connection_id = f"SequenceFlow_{connection_counter}"
                        connection_counter += 1

                        output_connections.append({
                            "id": connection_id,
                            "type": "sequenceFlow",
                            "name": connection_name if connection_name else "",
                            "sourceRef": source_ref,
                            "targetRef": target_id
                        })

    output_json = {
        "diagramId": diagram_id,
        "diagramName": diagram_name,
        "elements": output_elements,
        "connections": output_connections
    }

    return output_json

def remove_think_tags(text: str) -> str:
    return re.sub(
        pattern=r'<think>.*?</think>',
        repl='',
        string=text,
        flags=re.DOTALL
    )
def extract_error_message(text):
    keyword = "Value error, BPMN Logic Validation Failed:"
    index = text.find(keyword)
    if index == -1:
        return None
    return f"{keyword}\n"+text[index + len(keyword):]
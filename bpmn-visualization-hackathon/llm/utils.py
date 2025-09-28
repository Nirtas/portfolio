def extract_error_message(text):
    keyword = "Value error, BPMN Logic Validation Failed:"
    index = text.find(keyword)
    if index == -1:
        return None
    return f"{keyword}\n"+text[index + len(keyword):]

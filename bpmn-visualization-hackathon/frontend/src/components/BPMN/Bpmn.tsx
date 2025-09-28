// @ts-ignore
import ReactBpmn from "react-bpmn";

type Props = {
  bpmnDiagram?: string;
};
export const Bpmn = ({ bpmnDiagram }: Props) => {
  function onShown() {
    console.log("diagram shown");
  }

  function onLoading() {
    console.log("diagram loading");
  }
  // @ts-ignore
  function onError(err) {
    console.log("failed to show diagram");
  }

  return bpmnDiagram?.length ? (
    <ReactBpmn
      diagramXML={bpmnDiagram}
      onShown={onShown}
      onLoading={onLoading}
      onError={onError}
      autoFocus
    />
  ) : null;
};

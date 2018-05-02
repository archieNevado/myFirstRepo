import { init } from "./toggle";
import { addNodeDecoratorBySelector } from "@coremedia/js-node-decoration-service";

addNodeDecoratorBySelector(".toggle-item", init);

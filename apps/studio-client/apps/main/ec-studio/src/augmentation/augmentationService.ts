import AugmentationServiceImpl from "./AugmentationServiceImpl";
import IAugmentationService from "./IAugmentationService";

const augmentationService: IAugmentationService
        = new AugmentationServiceImpl();

export default augmentationService;

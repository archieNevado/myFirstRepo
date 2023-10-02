import BeanFactoryImpl from "@coremedia/studio-client.client-core-impl/data/impl/BeanFactoryImpl";
import JourneyImpl from "@coremedia/studio-client.main.bpbase-sfmc-p13n-studio/model/JourneyImpl";
import JourneysImpl from "@coremedia/studio-client.main.bpbase-sfmc-p13n-studio/model/JourneysImpl";

BeanFactoryImpl.initBeanFactory().registerRemoteBeanClasses(JourneyImpl, JourneysImpl);

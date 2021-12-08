
/**
 * Interface values for ResourceBundle "TopicPagesSettings".
 * @see TopicPagesSettings_properties#INSTANCE
 */
interface TopicPagesSettings_properties {

/**
 *######################################################################
 * The default folder path used when a new topic page is created
 *######################################################################
 */
  topic_pages_default_path: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "TopicPagesSettings".
 * @see TopicPagesSettings_properties
 */
const TopicPagesSettings_properties: TopicPagesSettings_properties = { topic_pages_default_path: "Navigation/TopicPages/" };

export default TopicPagesSettings_properties;

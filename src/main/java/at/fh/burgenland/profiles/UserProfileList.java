package at.fh.burgenland.profiles;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a list of user profiles. Extends ArrayList and implements Serializable. needed for
 * serialization of the user profiles to/from json.
 */
public class UserProfileList extends ArrayList<UserProfile> implements Serializable {}

package at.fh.burgenland.profiles;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Jackson annotations to support polymorphic JSON (de)serialization for {@link IfVoiceProfile}.
 *
 * <p>The {@link com.fasterxml.jackson.annotation.JsonTypeInfo} annotation specifies that a property
 * named <code>type</code> is included in the JSON to indicate the concrete implementation type.
 *
 * <p>The {@link com.fasterxml.jackson.annotation.JsonSubTypes} annotation defines the allowed
 * subtypes:
 *
 * <ul>
 *   <li>{@link VoiceProfile} with <code>type = "enum"</code>
 *   <li>{@link CustomVoiceProfile} with <code>type = "custom"</code>
 * </ul>
 *
 * <p>Example of a serialized object:
 *
 * <pre>
 * {
 *   "type": "custom",
 *   "minDb": -40,
 *   "maxDb": -10,
 *   "minFreq": 100,
 *   "maxFreq": 500
 * }
 * </pre>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = VoiceProfile.class, name = "enum"),
  @JsonSubTypes.Type(value = CustomVoiceProfile.class, name = "custom")
})
public interface IfVoiceProfile {

  int getMinFreq();

  int getMaxFreq();

  int getMinDb();

  int getMaxDb();
}

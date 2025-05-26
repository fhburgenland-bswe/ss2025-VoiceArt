package at.fh.burgenland.profiles;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = VoiceProfile.class, name = "enum"),
    @JsonSubTypes.Type(value = CustomVoiceProfile.class, name = "custom")
})

public interface IVoiceProfile {
  int getMinFreq();
  int getMaxFreq();
  int getMinDb();
  int getMaxDb();
}

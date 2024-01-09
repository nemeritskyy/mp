package ua.com.obox.dbschema.mark;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.obox.dbschema.translation.assistant.OnlyName;
import ua.com.obox.dbschema.translation.responsebody.Content;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"mark_id", "original_language", "translation_id", "color_hex", "emoji", "content"})
public class MarkResponse {
    @JsonProperty("mark_id")
    private String markId;
    @JsonProperty("original_language")
    private String originalLanguage;
    @JsonProperty("translation_id")
    private String translationId;
    @JsonProperty("color_hex")
    private String colorHex;
    @JsonProperty("emoji")
    private String emoji;
    @JsonUnwrapped
    private Content<OnlyName> content;
}

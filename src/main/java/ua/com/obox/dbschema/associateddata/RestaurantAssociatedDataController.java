package ua.com.obox.dbschema.associateddata;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.obox.dbschema.tools.logging.LoggingService;

@RestController
@RequestMapping("/associated")
@RequiredArgsConstructor
@Tag(name = "AssociatedData")
public class RestaurantAssociatedDataController {
    private final RestaurantAssociatedDataService associatedDataService;
    private final LoggingService loggingService;

    @GetMapping("/{associatedDataId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<RestaurantAssociatedDataResponse> getAssociatedDataById(@PathVariable String associatedDataId) {
        RestaurantAssociatedDataResponse associatedDataResponse = associatedDataService.getAssociatedDataById(associatedDataId);
        return ResponseEntity.ok(associatedDataResponse);
    }

    @DeleteMapping("/{restaurantId}")
    @Hidden
    public ResponseEntity<Void> deleteAssociatedDataByRestaurantId(@PathVariable String restaurantId) {
        associatedDataService.deleteAssociatedDataByRestaurantId(restaurantId);
        return ResponseEntity.noContent().build();
    }

}

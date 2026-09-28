package in.rahul.learning.controller;

import in.rahul.learning.commons.BaseResponse;
import in.rahul.learning.model.request.UrlRequest;
import in.rahul.learning.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url")
@Tag(name = "URL")
public class UrlController {

    private final UrlService  urlService;


    @Operation(
            summary = "Create short URL",
            description = "Creates a shortened URL"
    )
    @PostMapping
    public ResponseEntity<BaseResponse> createUrl(@RequestBody UrlRequest request) {
        return urlService.shorten(request.longUrl());
    }

    @Operation(
            summary = "Redirect using short URL",
            description = "Redirects to the original URL"
    )
    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String longurl = urlService.getUrl(code);

        System.out.println("Redirecting to " + URI.create(longurl));
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(longurl))
                .build();
    }
}
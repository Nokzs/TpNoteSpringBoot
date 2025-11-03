package com.example.demo.dto.evaluationDto;

import java.util.ArrayList;
import java.util.List;

public record EvaluationSignedUrlDto(ArrayList<String> keys, List<String> urls) {
}

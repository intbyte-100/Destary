package com.intbyte.destary.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(@NotBlank @Size(max = 64) String content){
}

package com.atipera.searcher.dto.github;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the response structure for a GitHub repository.
 * This class includes the repository's name, whether it is forked, and the owner.
 */
@Getter
@Setter
@Builder
public class RepositoryResponse {
    private String name;
    @SerializedName("fork")
    private boolean isForked;
    private OwnerResponse owner;
}

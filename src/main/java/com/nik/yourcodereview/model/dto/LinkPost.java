package com.nik.yourcodereview.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;


public class LinkPost {

    @JsonProperty("original")
    private String original;

    public LinkPost original(String original) {
        this.original = original;
        return this;
    }

    /**
     * given original link
     *
     * @return original
     */
    @Valid
    @NotBlank
    @Size(min = 1, max = 2048)
    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LinkPost linkPost = (LinkPost) o;
        return Objects.equals(this.original, linkPost.original);
    }

    @Override
    public int hashCode() {
        return Objects.hash(original);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LinkPost {\n");

        sb.append("    original: ").append(toIndentedString(original)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}


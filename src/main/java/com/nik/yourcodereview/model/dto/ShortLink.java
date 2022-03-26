package com.nik.yourcodereview.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class ShortLink {
    @JsonProperty("link")
    private String link;

    public ShortLink link(String link) {
        this.link = link;
        return this;
    }

    /**
     * given link link
     *
     * @return link
     */
    @NotNull
    @Valid
    @Size(min = 1, max = 10)
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShortLink shortLink = (ShortLink) o;
        return Objects.equals(this.link, shortLink.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ShortLink {\n");

        sb.append("    link: ").append(toIndentedString(link)).append("\n");
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


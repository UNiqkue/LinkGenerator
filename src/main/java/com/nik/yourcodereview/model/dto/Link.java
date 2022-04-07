package com.nik.yourcodereview.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class Link {
    @JsonProperty("link")
    private String link;

    @JsonProperty("original")
    private String original;

    @JsonProperty("rank")
    private Long rank;

    @JsonProperty("count")
    private Long count;

    public Link link(String link) {
        this.link = link;
        return this;
    }

    /**
     * short generated link
     *
     * @return link
     */
    @NotBlank
    @Valid
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Link original(String original) {
        this.original = original;
        return this;
    }

    /**
     * given original link
     *
     * @return original
     */
    @NotBlank
    @Valid
    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public Link rank(Long rank) {
        this.rank = rank;
        return this;
    }

    /**
     * место ссылки в топе запросов
     *
     * @return rank
     */
    @NotBlank
    @Valid
    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

    public Link count(Long count) {
        this.count = count;
        return this;
    }

    /**
     * число запросов по короткой ссылке
     *
     * @return count
     */
    @NotBlank
    @Valid
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Link link = (Link) o;
        return Objects.equals(this.link, link.link) &&
                Objects.equals(this.original, link.original) &&
                Objects.equals(this.rank, link.rank) &&
                Objects.equals(this.count, link.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link, original, rank, count);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Link {\n");

        sb.append("    link: ").append(toIndentedString(link)).append("\n");
        sb.append("    original: ").append(toIndentedString(original)).append("\n");
        sb.append("    rank: ").append(toIndentedString(rank)).append("\n");
        sb.append("    count: ").append(toIndentedString(count)).append("\n");
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


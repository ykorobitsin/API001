package model;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 25.05.13
 * Time: 16:35
 */
public class AdvertModel {

    private Integer id;
    private String url;
    private Boolean isIndexed;

    public AdvertModel(Integer id, String url, Boolean isIndexed) {
        this.id = id;
        this.url = url;
        this.isIndexed = isIndexed;
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Boolean isIndexed() {
        return isIndexed;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof AdvertModel)) {
            return false;
        }

        AdvertModel that = (AdvertModel) obj;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        if (isIndexed != null ? !isIndexed.equals(that.isIndexed) : that.isIndexed != null) {
            return false;
        }

        //noinspection RedundantIfStatement
        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (isIndexed != null ? isIndexed.hashCode() : 0);
        return result;
    }
}

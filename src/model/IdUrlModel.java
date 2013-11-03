package model;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 25.05.13
 * Time: 16:35
 */
public class IdUrlModel {

    private Integer id;
    private String url;

    public IdUrlModel(Integer id, String url) {
        this.id = id;
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof IdUrlModel)) {
            return false;
        }

        IdUrlModel that = (IdUrlModel) obj;

        if (id != null ? !id.equals(that.id) : that.id != null) {
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
        return result;
    }
}

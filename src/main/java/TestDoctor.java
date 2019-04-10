import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "test0409", schema = "mysql", catalog = "")
public class TestDoctor {
    private String name;
    private String hospital;
    private String link;
    private String nownumber;

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "hospital")
    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    @Basic
    @Column(name = "link")
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Basic
    @Column(name = "nownumber")
    public String getNownumber() {
        return nownumber;
    }

    public void setNownumber(String nownumber) {
        this.nownumber = nownumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestDoctor that = (TestDoctor) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (hospital != null ? !hospital.equals(that.hospital) : that.hospital != null) return false;
        if (link != null ? !link.equals(that.link) : that.link != null) return false;
        if (nownumber != null ? !nownumber.equals(that.nownumber) : that.nownumber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (hospital != null ? hospital.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (nownumber != null ? nownumber.hashCode() : 0);
        return result;
    }
}

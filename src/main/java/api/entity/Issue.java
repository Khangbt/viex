package api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by tiago on 07/10/2017.
 */

@Entity
@Table(name = "issue")
public class Issue implements Serializable {
    @Id
    @Column(unique = true, name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "created_at" )
    private Date createdAt;

    @Column(name = "decscription")
    private String decscription;

    @Column(name = "key")
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDecscription() {
        return decscription;
    }

    public void setDecscription(String decscription) {
        this.decscription = decscription;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orm;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Christoph
 */
@Entity
@Table(name = "upload_record")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UploadRecord.findAll", query = "SELECT u FROM UploadRecord u"),
    @NamedQuery(name = "UploadRecord.findById", query = "SELECT u FROM UploadRecord u WHERE u.id = :id"),
    @NamedQuery(name = "UploadRecord.findByFilename", query = "SELECT u FROM UploadRecord u WHERE u.filename = :filename")})
public class UploadRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "filename")
    private String filename;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "tags")
    private String tags;

    public UploadRecord() {
    }
    
    public UploadRecord(String filename, String tags) {
        this.filename = filename;
        this.tags = tags;
    }

    public UploadRecord(Integer id) {
        this.id = id;
    }

    public UploadRecord(Integer id, String filename, String tags) {
        this.id = id;
        this.filename = filename;
        this.tags = tags;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UploadRecord)) {
            return false;
        }
        UploadRecord other = (UploadRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UploadRecord: filename: "+this.filename+"; tags:"+this.tags;
    }
    
}

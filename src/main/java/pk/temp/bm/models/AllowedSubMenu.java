package pk.temp.bm.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="allowed_submenu")
@NamedQuery(name="AllowedSubMenu.findAll", query="SELECT o FROM AllowedSubMenu o")
@JsonIdentityInfo(
	       generator = ObjectIdGenerators.PropertyGenerator.class,
	       property = "id")
public class AllowedSubMenu implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="authority_id")
	private Role userRole;
	
	@ManyToOne
	@JoinColumn(name="submenu_id")
	private SideSubMenu side_submenu;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Role getRole() {
		return userRole;
	}

	public void setRole(Role role) {
		this.userRole = role;
	}

	public SideSubMenu getSide_submenu() {
		return side_submenu;
	}

	public void setSide_submenu(SideSubMenu side_submenu) {
		this.side_submenu = side_submenu;
	}

	@Override
	public String toString() {
		return "AllowedSubMenu [id=" + id +  ", side_submenu=" + side_submenu + "]";
	}
	
}

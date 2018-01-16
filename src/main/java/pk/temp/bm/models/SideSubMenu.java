package pk.temp.bm.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;


@Entity
@Table(name="side_submenu")
@NamedQuery(name="SideSubMenu.findAll", query="SELECT o FROM SideSubMenu o")
@JsonIdentityInfo(
	       generator = ObjectIdGenerators.PropertyGenerator.class,
	       property = "id")
public class SideSubMenu implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="state")
	private String state;

	@Column(name = "permission_name")
    private String permissionName;
    
    private int parent_id;
    
    private int level;
	
	@OneToMany(mappedBy="side_submenu", fetch=FetchType.LAZY)
	private List<AllowedSubMenu> allowed_submenu;
	

	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getParent_id() {
		return parent_id;
	}

	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}

	public List<AllowedSubMenu> getAllowed_submenu() {
		return allowed_submenu;
	}

	public void setAllowed_submenu(List<AllowedSubMenu> allowed_submenu) {
		this.allowed_submenu = allowed_submenu;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "SideSubMenu [id=" + id + ", name=" + name + ", state=" + state
				+ ", parent_id=" + parent_id + "]";
	}


	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
}

package pk.temp.bm.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
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
@Table(name="side_menu")
@NamedQuery(name="SideMenu.findAll", query="SELECT o FROM SideMenu o")
@JsonIdentityInfo(
	       generator = ObjectIdGenerators.PropertyGenerator.class,
	       property = "id")
public class SideMenu implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="icon")
	private String icon;
	
	@Column(name="state")
	private String state;

	@Column(name = "permission_name")
	private String permissionName;
	

	

	@OneToMany(mappedBy="sideMenu" , cascade=CascadeType.ALL , fetch=FetchType.LAZY)
	private List<AllowedMenu> allowed_menu;
	
	
	
	
	
	

	public List<AllowedMenu> getAllowed_menu() {
		return allowed_menu;
	}

	public void setAllowed_menu(List<AllowedMenu> allowed_menu) {
		this.allowed_menu = allowed_menu;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}


	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
}

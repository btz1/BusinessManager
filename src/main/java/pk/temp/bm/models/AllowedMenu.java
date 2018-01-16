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
import com.fasterxml.jackson.annotation.ObjectIdGenerators;


@Entity
@Table(name="allowed_menu")
@NamedQuery(name="AllowedMenu.findAll", query="SELECT o FROM AllowedMenu o")
@JsonIdentityInfo(
	       generator = ObjectIdGenerators.PropertyGenerator.class,
	       property = "id")
public class AllowedMenu implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int id;
	
	@ManyToOne
	@JoinColumn(name="authority_id")
	private Role userRole;
	
	@ManyToOne
	@JoinColumn(name="sidemenu_id")
	private SideMenu sideMenu;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Role getUserRole() {
		return userRole;
	}

	public void setUserRole(Role role) {
		this.userRole = role;
	}

	public SideMenu getSideMenu() {
		return sideMenu;
	}

	public void setSideMenu(SideMenu side_menu) {
		this.sideMenu = side_menu;
	}
	

}


package com.api.backend.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "Ship")
public class ShipEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shipId;
	private String shipName;
	private int shipLength;
	
}

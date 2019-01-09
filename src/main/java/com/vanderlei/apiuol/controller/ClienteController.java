package com.vanderlei.apiuol.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.vanderlei.apiuol.entity.Cliente;
import com.vanderlei.apiuol.helper.Location;
import com.vanderlei.apiuol.repository.ClienteRepository;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(path = "/clientes")
public class ClienteController {

	@Autowired
	private ClienteRepository clientes;
	
	@GetMapping()
	@ApiOperation(value="get all customers",response=Cliente.class)
    @ApiResponses(value={
	   @ApiResponse(code=200,message="Return details all customers",response=Cliente.class),
	   @ApiResponse(code=500,message="Internal Server Error")	  
    })
	public Iterable<Cliente> list() {
		return clientes.findAll();		
	}
	
	@GetMapping("/{id}")
	@ApiOperation(value="get customer",response=Cliente.class)
    @ApiResponses(value={
	   @ApiResponse(code=200,message="Return details customers",response=Cliente.class),
	   @ApiResponse(code=500,message="Internal Server Error"),
	   @ApiResponse(code=404,message="Customer not found")
    })
	public ResponseEntity<Optional<Cliente>> listById(@PathVariable Long id) {
		if(!clientes.existsById(id))
			return ResponseEntity.notFound().build();
		
		Optional<Cliente> cliente = clientes.findById(id);
		
		return ResponseEntity.ok(cliente);
	}
	
	@PostMapping
	@ApiOperation(value="set a customer",response=Cliente.class)
    @ApiResponses(value={
	   @ApiResponse(code=200,message="Return details customer created."
	   		+ "The values 'minTemp' and 'maxTemp' are automatically obtained through the requisition",response=Cliente.class),
	   @ApiResponse(code=500,message="Internal Server Error")
    })
	public Cliente create(@Valid @RequestBody Cliente cliente, HttpServletRequest request) throws IOException, UnirestException {
		String ipAddress = this.getIp(request); 
		
		Location location = new Location();
		location.getLocationByIp(ipAddress);
		
		cliente.setIp(ipAddress);		
		cliente.setMinTemp(location.getMinTemp());
		cliente.setMaxTemp(location.getMaxTemp());
		
		return clientes.save(cliente);
	}
	
	@DeleteMapping("/{id}")
	@ApiOperation(value="delete a customer",response=Cliente.class)
    @ApiResponses(value={	   
	   @ApiResponse(code=500,message="Internal Server Error"),
	   @ApiResponse(code=204,message="No content")
    })
	public ResponseEntity<Void> deleteById(@PathVariable Long id) {
		Optional<Cliente> cliente = clientes.findById(id);
		
		if(cliente ==  null)
			return ResponseEntity.notFound().build();
		
		clientes.deleteById(id);
		
		return ResponseEntity.noContent().build();
	}
	
	@PutMapping("/{id}")
	@ApiOperation(value="update a customer",response=Cliente.class)
    @ApiResponses(value={
	   @ApiResponse(code=200,message="Return details customer updated."
	   		+ "The values 'minTemp' and 'maxTemp' are automatically obtained through the requisition",response=Cliente.class),
	   @ApiResponse(code=500,message="Internal Server Error"),
	   @ApiResponse(code=404,message="Customer not found")
    })
	public ResponseEntity<Cliente> updateById(@PathVariable Long id, @Valid @RequestBody Cliente cliente, HttpServletRequest request) {
		if(!clientes.existsById(id))
			return ResponseEntity.notFound().build();

		//prepare data for update
		cliente.setId(id);
		cliente.setIp(this.getIp(request));
		clientes.save(cliente);
		
		return ResponseEntity.ok(cliente);
	}
	
	private String getIp(HttpServletRequest request) {
		String remoteAddr = "";
        
		if (request != null) {
            remoteAddr = request.getHeader("X-Forwarded-For");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
                System.out.println("IP local details"+ request.getRemoteAddr());
                remoteAddr = request.getRemoteAddr();
                if (remoteAddr.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
                    InetAddress inetAddress = null;
					try {
						inetAddress = InetAddress.getLocalHost();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    String ipAddress = inetAddress.getHostAddress();
                    remoteAddr = ipAddress;
                }
            }
        }
		
		return remoteAddr;
	}
}
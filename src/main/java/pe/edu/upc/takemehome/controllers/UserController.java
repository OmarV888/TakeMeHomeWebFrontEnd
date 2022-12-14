package pe.edu.upc.takemehome.controllers;

//import com.fasterxml.jackson.datatype.jsr310.ser.YearSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.takemehome.entities.Comment;
import pe.edu.upc.takemehome.entities.Order;
import pe.edu.upc.takemehome.entities.Shipment;
import pe.edu.upc.takemehome.entities.User;
import pe.edu.upc.takemehome.exceptions.ResourceNotFoundException;
import pe.edu.upc.takemehome.repositories.CommentRepository;
import pe.edu.upc.takemehome.repositories.OrderRepository;
import pe.edu.upc.takemehome.repositories.ShipmentRepository;
import pe.edu.upc.takemehome.repositories.UserRepository;

import java.text.ParseException;
//import java.text.SimpleDateFormat;
import java.util.List;
//import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private CommentRepository commentRepository;


    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users=userRepository.findAll();
        for (User u : users) {
           u.setShipments(null);
           u.setOrders(null);
           u.setCommentsSend(null);
           u.setCommentsReceived(null);
           u.setNotifications(null);
        }
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }
    @GetMapping("/users/orders/shipments")
    public ResponseEntity<List<User>> getAllUsersWithShipmentsAndOrders(){
        List<User> users=userRepository.findAll();
        for (User u : users) {
            for (Order o : u.getOrders()) {
                o.setUser(null);
                o.setShipment(null);
            }
            for (Shipment s : u.getShipments()) {
                s.setUser(null);
                s.setOrder(null);
            }
            u.setCommentsSend(null);
            u.setCommentsReceived(null);
            u.setNotifications(null);
            u.setCommentsSupport(null);
        }

        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }



    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id){

        User user=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Not found Owner with id="+id));

        user.setOrders(null);
        user.setShipments(null);
        user.setCommentsSend(null);
        user.setCommentsReceived(null);
        user.setNotifications(null);
        user.setCommentsSupport(null);
        return new ResponseEntity<User>(user,HttpStatus.OK);
    }
    @GetMapping("/users/{id}/orders")
    public ResponseEntity<User> getUserByIdWithOrders(@PathVariable("id") Long id){

        User user=userRepository.findById(id).
                orElseThrow(()->new ResourceNotFoundException("Not found Owner with id="+id));

        for (Order o : user.getOrders()) {
            o.setUser(null);
            o.setShipment(null);
        }
        user.setShipments(null);
        user.setCommentsSend(null);
        user.setCommentsReceived(null);
        user.setNotifications(null);
        user.setCommentsSupport(null);
        return new ResponseEntity<User>(user,HttpStatus.OK);
    }

    @GetMapping("/users/{id}/shipments")
    public ResponseEntity<User> getUserByIdWithShipments(@PathVariable("id") Long id){

        User user=userRepository.findById(id).
                orElseThrow(()->new ResourceNotFoundException("Not found Owner with id="+id));

        for (Shipment o : user.getShipments()) {
            o.getUser().setShipments(null);
            o.getUser().setOrders(null);
            o.getUser().setCommentsReceived(null);
            o.getUser().setCommentsSend(null);
            o.getUser().setNotifications(null);
            o.getUser().setCommentsSupport(null);
            o.getOrder().setShipment(null);
            o.getOrder().setUser(null);
        }
        user.setOrders(null);
        user.setCommentsSend(null);
        user.setCommentsReceived(null);
        user.setNotifications(null);
        user.setCommentsSupport(null);
        return new ResponseEntity<User>(user,HttpStatus.OK);
    }

    @GetMapping("/users/username/{username}/password/{password}")
    public ResponseEntity<User> userAccount(@PathVariable("username") String username, @PathVariable("password") String psw ){
        User userFound=userRepository.findByUsernameAndPassword(username,psw);
        userFound.setOrders(null);
        userFound.setShipments(null);
        userFound.setCommentsSend(null);
        userFound.setCommentsReceived(null);
        userFound.setNotifications(null);
        userFound.setCommentsSupport(null);
        return new ResponseEntity<User>(userFound,HttpStatus.OK);
    }


    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) throws ParseException {
        //SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy");
        User updateUser = userRepository.findByUsername(user.getUsername());
        if (updateUser == null) {
            User newUser = userRepository.save(new User(user.getName(), user.getUsername(), user.getEmail(),user.getUrlImage(), user.getPhone(), user.getBirthday(), user.getCountry(), user.getPassword()));
            return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<User>(updateUser, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUserById(@PathVariable("id") Long id){

        User user=userRepository.findById(id).
                orElseThrow(()->new ResourceNotFoundException("Not found Owner with id="+id));
        for(Order o: user.getOrders()){
            orderRepository.delete(o);
        }
        for(Shipment o: user.getShipments()){
            shipmentRepository.delete(o);
        }
        for(Comment c:user.getCommentsSend()){
            commentRepository.delete(c);
        }
        for(Comment c:user.getCommentsReceived()){
            commentRepository.delete(c);
        }

        userRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable("id") Long id, @RequestBody User user) {

        User updateUser =  userRepository.findById(id).
                orElseThrow(()->new ResourceNotFoundException("Not found Owner with id="+id));
        if (user.getPhone()!=null)
            updateUser.setPhone(user.getPhone());
        if (user.getEmail()!=null)
            updateUser.setEmail(user.getEmail());
        if (user.getCountry()!=null)
            updateUser.setCountry(user.getCountry());
        User updatedUser =userRepository.save(updateUser);
        updatedUser.setOrders(null);
        updateUser.setShipments(null);
        updateUser.setCommentsSend(null);
        updateUser.setCommentsReceived(null);
        updateUser.setNotifications(null);
        updateUser.setCommentsSupport(null);
        return new ResponseEntity<User>(updatedUser,HttpStatus.OK);

    }
    @PutMapping("/users/{id}/password")
    public ResponseEntity<User> updatePasswordById(@PathVariable("id") Long id, @RequestBody User user) {

        User updateUser =  userRepository.findById(id).
                orElseThrow(()->new ResourceNotFoundException("Not found Owner with id="+id));
        if (user.getPassword()!=null)
            updateUser.setPassword(user.getPassword());

        User updatedUser =userRepository.save(updateUser);
        updatedUser.setOrders(null);
        updateUser.setShipments(null);
        updateUser.setCommentsReceived(null);
        updateUser.setCommentsSend(null);
        updateUser.setNotifications(null);
        updateUser.setCommentsSupport(null);
        return new ResponseEntity<User>(updatedUser,HttpStatus.OK);

    }
    @PutMapping("/users/{id}/image")
    public ResponseEntity<User> updateImgById(@PathVariable("id") Long id, @RequestBody User user) {

        User updateUser =  userRepository.findById(id).
                orElseThrow(()->new ResourceNotFoundException("Not found Owner with id="+id));
        if (user.getUrlImage()!=null)
            updateUser.setUrlImage(user.getUrlImage());

        User updatedUser =userRepository.save(updateUser);
        updatedUser.setOrders(null);
        updateUser.setShipments(null);
        updateUser.setCommentsReceived(null);
        updateUser.setCommentsSend(null);
        updateUser.setNotifications(null);
        updateUser.setCommentsSupport(null);
        return new ResponseEntity<User>(updatedUser,HttpStatus.OK);

    }
    @PutMapping("/users/{id}/cont")
    public ResponseEntity<User> updateContUser(@PathVariable("id") Long id, @RequestBody User user) {

        User updateUser =  userRepository.findById(id).
                orElseThrow(()->new ResourceNotFoundException("Not found Owner with id="+id));
        if (user.getCont()!=null){
            updateUser.setCont(user.getCont());
        }

        User updatedUser =userRepository.save(updateUser);
        updatedUser.setOrders(null);
        updateUser.setShipments(null);
        updateUser.setCommentsReceived(null);
        updateUser.setCommentsSend(null);
        updateUser.setNotifications(null);
        updateUser.setCommentsSupport(null);
        return new ResponseEntity<User>(updatedUser,HttpStatus.OK);

    }

}

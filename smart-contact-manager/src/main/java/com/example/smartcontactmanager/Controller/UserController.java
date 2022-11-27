package com.example.smartcontactmanager.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.example.smartcontactmanager.dao.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.smartcontactmanager.Helper.Message;
//import com.example.smartcontactmanager.dao.ContactRepository;
import com.example.smartcontactmanager.dao.UserRepository;
import com.example.smartcontactmanager.entities.Contact;
import com.example.smartcontactmanager.entities.User;



import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired

    private ContactRepository contactRepository;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal){
        String usernmae = principal.getName();
        System.out.println(usernmae);

        User user = userRepository.getUserByName(usernmae);
        model.addAttribute("user", user);
    }

    @RequestMapping("/index")
    public String dashboard(Model model,Principal principal){
        
        model.addAttribute("title", "Home");
        return "normal/user_dashboard";
    }

    @GetMapping("/add-contact")
    public String addContact(Model model){
        model.addAttribute("title", "add-contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact-from";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage")MultipartFile file, Principal principal,HttpSession session){


        try{
            String name = principal.getName();
            User user =  this.userRepository.getUserByName(name);


//            file uploading
            if(file.isEmpty()){
                System.out.println("file is empty");
                contact.setImage("contact.png");

            }else{
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("/static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is uploaded");
            }

            contact.setUser(user);
            user.getContacts().add(contact);
            this.userRepository.save(user);

//        System.out.println(contact);
            System.out.println("database updated");

            //success message
            session.setAttribute("message", new Message("Data added", "success"));


        }catch (Exception e){
            System.out.println("Error"+e.getMessage());
            //failure message
            session.setAttribute("message", new Message("Something went wrong", "danger"));


        }

        return "normal/add_contact-from";
    }


    //show contacts handeler
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page")Integer page,Model model,Principal principal){
        model.addAttribute("title","show-contacts");

        //contacts list (1st method)
        // String userName = principal.getName();
        // User user = this.userRepository.getUserByName(userName);
        //  List<Contact> contacts = user.getContacts();

        // 2nd method
        String userName = principal.getName();
        User user = this.userRepository.getUserByName(userName);
        PageRequest pageable=PageRequest.of(page, 5);

        Page<Contact> contacts =  this.contactRepository.findContactsByUser(user.getId(),pageable);


        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);

        model.addAttribute("totalPages", contacts.getTotalPages());

        return "normal/show_contacts";
    }

    // single contact details

    @RequestMapping("/{cid}/contact")
    public String showContactDetails(@PathVariable("cid") Integer cid, Model model,Principal principal){

        System.out.println("cID" + cid);
        String userName = principal.getName();
        User user = this.userRepository.getUserByName(userName);

        Optional<Contact> contactOptional =  this.contactRepository.findById(cid);
        Contact contact= contactOptional.get();

        if(user.getId()==contact.getUser().getId()){
            model.addAttribute("title",contact.getName());
            model.addAttribute("contact", contact);
        }
        
        return "normal/contact_detail";
    } 

    // delete handler
    @GetMapping("/delete/{cid}")
    @Transactional
    public String deleteContact(@PathVariable("cid") Integer cId,Model model,HttpSession session,Principal principal){
        

        Optional<Contact> contactOptional=  this.contactRepository.findById(cId);
        Contact contact= contactOptional.get();

        //Contact contact = this.contactRepository.findby(cId).get();
        // contact.setUser(null);
        User user = this.userRepository.getUserByName(principal.getName());
        user.getContacts().remove(contact);

        this.userRepository.save(user);

        this.contactRepository.delete(contact);
        session.setAttribute("message", new Message("Contact deleted succesfully", "success"));

        return "redirect:/user/show-contacts/0";

    }

    //update contacts

    @PostMapping("/update-contact/{cid}")
    public String updateContact(@PathVariable("cid") Integer cid,Model model){

        model.addAttribute("title", "update-from");

        Contact contact = this.contactRepository.findById(cid).get();
        model.addAttribute("contact", contact);
        return "normal/update_form";
    }

    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, Model model,Principal principal,HttpSession session){

        Contact oldContact =this.contactRepository.findById(contact.getCid()).get();
        try {
            
            if(!file.isEmpty()){


                //delete old photo
                File deletFile = new ClassPathResource("/static/img").getFile();
                File file1 = new File(deletFile, oldContact.getImage());
                file1.delete();

                //update new photo
                File saveFile = new ClassPathResource("/static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());

            }else{
                contact.setImage(oldContact.getImage());
            }

            User user = this.userRepository.getUserByName(principal.getName());
            contact.setUser(user);

            this.contactRepository.save(contact);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/user/"+contact.getCid()+"/contact";
    }

    //your profile view
    @GetMapping("/profile")
    public String yourProfile(Model model){
        model.addAttribute("title", "profile page");
        return "normal/profile";

    }
}

package srr.srr.Reservation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class ReservationController {
    @Autowired
    private ReservationRepo reservationRepo;
    private ReservationService reservationService;
    List <ReservationEntity> reservation;

    @GetMapping("/")
    public String getRole(Model model) 
    {
        model.addAttribute("content", "fragments/Role");
        return "index";
    }
    @GetMapping("/customer")
    public String customer_table(Model model) {
        reservation = reservationRepo.findAllByOrderByTimeDesc();
        model.addAttribute("content", "fragments/customer/TableCus");
        model.addAttribute("reservations", new ReservationDto());
        model.addAttribute("reservation", reservation);
        return "index";
    }
    @GetMapping("/admin")
    public String admin_table(Model model) {
        reservation = reservationRepo.findAll();
        model.addAttribute("content", "fragments/admin/TableManag");
        model.addAttribute("reservations", new ReservationDto());
        model.addAttribute("reservation", reservation);
        return "index";
    }

    @GetMapping("/formAdmin")
    public String formAdmin(Model model) {
        model.addAttribute("content", "fragments/admin/CreateTableAdmin");
        model.addAttribute("reservations", new ReservationDto());
        return "index";
    }
    @GetMapping("/formCus")
    public String formCus(Model model) {
        model.addAttribute("content", "fragments/customer/ReservTable");
        model.addAttribute("reservations", new ReservationDto());
        return "index";
    }

    @PostMapping("/saveTable")
    public String saveTable(@Valid @ModelAttribute("reservation") ReservationDto reservationDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            model.addAttribute("content", "fragments/formAdmin");
            return "index";
        }

        ReservationEntity saveReservation = reservationService.saveReservation(reservationDto);
        if(reservationDto.getTableId() != null){
            saveReservation = reservationRepo.findById(reservationDto.getTableId()).orElse(new ReservationEntity());
        }
        else{
            saveReservation = new ReservationEntity();
        }

        saveReservation.setCustomerName(reservationDto.getCustomerName());
        saveReservation.setPhoneNumber(reservationDto.getPhoneNumber());
        saveReservation.setTime(reservationDto.getTime());

        reservationRepo.save(saveReservation);
        System.out.println("Saving ReservationEntity: " + saveReservation);
        return "redirect:/admin";
    }

    @GetMapping("/update/{tableId}")
    public String updateTable(@PathVariable("tableId") Long tableId, Model model) {
        ReservationEntity reservationEntity = reservationRepo.findById(tableId).get();
        model.addAttribute("content", "fragments/");
        model.addAttribute("reservation", reservationEntity);
        return "index";
    }
    @GetMapping("/delete/{tableId}")
    public String getMethodName(@PathVariable("tableId") Long tableId) {
        reservationRepo.deleteById(tableId);
        return "redirect:/admin";
    }

}

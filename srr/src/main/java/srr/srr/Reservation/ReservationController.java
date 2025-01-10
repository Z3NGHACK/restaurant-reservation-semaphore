package srr.srr.Reservation;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ReservationController {
    @Autowired
    private ReservationRepo reservationRepo;
    @Autowired
    private ReservationService reservationService;
    List<ReservationEntity> reservation;

    @GetMapping("/")
    public String getRole(Model model) {
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
    public String saveTable(@Valid @ModelAttribute("reservations") ReservationDto reservationDto,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            //fix the fragment content path
            model.addAttribute("content", "fragments/admin/CreateTableAdmin");
            return "index";
        }

        ReservationEntity reservationEntity;
        if (reservationDto.getId() != null) {
            reservationEntity = reservationRepo.findById(reservationDto.getId()).orElse(new ReservationEntity());
        } else {
            reservationEntity = new ReservationEntity();
        }
        reservationEntity.setTableId(reservationDto.getTableId());
        reservationEntity.setCustomerName(reservationDto.getCustomerName());
        reservationEntity.setPhoneNumber(reservationDto.getPhoneNumber());
        reservationEntity.setTime(reservationDto.getTime());

        reservationRepo.save(reservationEntity);
        return "redirect:/admin";
    }
    @GetMapping("/update/{id}")
    public String updateTable(@PathVariable("id") Long id, Model model) {
        ReservationEntity reservationEntity = reservationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation ID: " + id));
        ReservationDto reservationDto = new ReservationDto(reservationEntity);
        model.addAttribute("content", "fragments/admin/CreateTableAdmin");
        model.addAttribute("reservations", reservationDto);
        return "index";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, @Valid @ModelAttribute ReservationDto reservationDto,
                                BindingResult bindingResult, Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("content", "fragments/admin/CreateTableAdmin");
            return "index";
        }

        ReservationEntity reservationEntity = reservationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation ID: " + id));

        reservationEntity.updateFromDTO(reservationDto);
        reservationRepo.save(reservationEntity);

        return "redirect:/admin";
    }


    @GetMapping("/delete/{id}")
    public String getMethodName(@PathVariable("id") Long id) {
        if (reservationRepo.existsById(id)) {
            reservationRepo.deleteById(id);
            return "redirect:/admin";
        } else {
            throw new IllegalArgumentException("Invalid product ID: " + id);
        }
       
    }

}

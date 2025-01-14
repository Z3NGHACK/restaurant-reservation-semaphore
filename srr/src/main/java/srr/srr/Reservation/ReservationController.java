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
    private final ReservationService reservationService;
    List<ReservationEntity> reservation;

      // Constructor injection for ReservationService
      public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
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
        // Fix the fragment content path
        if ("admin".equals(reservationDto.getUserType())) {
            model.addAttribute("content", "fragments/admin/CreateTableAdmin");
        } else {
            model.addAttribute("content", "fragments/customer/ReservTable");
        }
        return "index";
    }

    // Check if a reservation with the same tableId and customerName already exists
    ReservationEntity existingReservation = reservationRepo.findByTableIdAndCustomerName(
            reservationDto.getTableId(), reservationDto.getCustomerName());

    if (existingReservation != null) {
        model.addAttribute("content", "fragments/customer/ReservTable");
        model.addAttribute("error", "This table is already reserved by the customer.");
        return "index";
    }

    ReservationEntity reservationEntity;
    if (reservationDto.getId() != null) {
        reservationEntity = reservationRepo.findById(reservationDto.getId()).orElse(new ReservationEntity());
    } else {
        reservationEntity = new ReservationEntity();
    }

    // Check if the customer already has an active reservation (Pending status)
    if (reservationRepo.existsByCustomerNameAndStatus(reservationDto.getCustomerName(), ReservationStatus.PENDING)) {
        model.addAttribute("content", "fragments/customer/ReservTable");
        model.addAttribute("error", "You already have an active reservation.");
        return "index";
    }

    reservationEntity.setTableId(reservationDto.getTableId());
    reservationEntity.setCustomerName(reservationDto.getCustomerName());
    reservationEntity.setPhoneNumber(reservationDto.getPhoneNumber());
    reservationEntity.setTime(reservationDto.getTime());

    reservationRepo.save(reservationEntity);

    // Redirect based on user type
    if ("admin".equals(reservationDto.getUserType())) {
        // Admin view
        return "redirect:/admin";
    } else {
        // Customer view
        return "redirect:/customer";
    }
}

    @GetMapping("/update/{id}")
    public String updateTable(@PathVariable("id") Long id, Model model) {
        ReservationEntity reservationEntity = reservationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation ID: " + id));
                System.out.println("Reservation Status: " + reservationEntity.getStatus());
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

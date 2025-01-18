package srr.srr.Reservation;

import java.io.IOException;
import java.util.List;
// import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



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


        ReservationEntity reservationEntity = reservationDto.getId() != null
        ? reservationRepo.findById(reservationDto.getId()).orElse(new ReservationEntity())
        : new ReservationEntity();


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
    @PostMapping("/confirm/{id}")
    public String confirmReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        System.out.println("Confirming reservation with ID: " + id); // Log for debugging
        String response = reservationService.confirmReservation(id);
        redirectAttributes.addFlashAttribute("alertMessage", response); // Store the alert message
        return "redirect:/admin"; // Redirect to the admin page
    }
    
    @PostMapping("/release/{id}")
    public String releaseAndAutoConfirm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        System.out.println("Releasing reservation with ID: " + id); // Log for debugging
        String response = reservationService.releaseAndAutoConfirm();
        redirectAttributes.addFlashAttribute("alertMessage", response); // Store the alert message
        return "redirect:/admin"; // Redirect to the admin page
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
}

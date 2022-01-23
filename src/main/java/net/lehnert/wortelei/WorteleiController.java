package net.lehnert.wortelei;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@SessionAttributes("wortelei")
public class WorteleiController {

    @GetMapping("/wortelei")
    public String worteleiForm(Model model) {
        if (!model.containsAttribute("wortelei")) {
            model.addAttribute("wortelei", new Wortelei());
        }
        return "wortelei";
    }

    @GetMapping("/neu")
    public String neu(Model model) {
        model.addAttribute("wortelei", new Wortelei());
        return "wortelei";
    }

    @PostMapping("/wortelei")
    public String worteleiSubmit(@ModelAttribute("wortelei") Wortelei wortelei, Model model) {
        String fehler = wortelei.addVersuch();
        if (fehler != null) {
            model.addAttribute("fehler", fehler);
        } else {
            if (wortelei.isGeloest()) {
                return "geloest";
            }
            if (wortelei.keineVersucheMehrOffen()) {
                return "nichtgeschafft";
            }
        }
        wortelei.setVersuch("");
        return "wortelei";
    }
}

package com.movie_theater.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    @GetMapping(value = {"/home", "/"})
    public String getHome() {
        return "newHome";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "redirect:/home";
    }

    @GetMapping("/forgot-password")
    public String getForgotPassword() {
        return "forgotPassword";
    }

    @GetMapping("/account-information")
    public String getEditAccountInformation() {
        return "editAccountInformation";
    }

    @GetMapping("/booked-Ticket")
    public String getBookedTicket() {
        return "bookedTicket";
    }

    @GetMapping("/admin/statistic")
    public String getStatistic() {
        return "statistic";
    }


    @GetMapping("/admin/movie-list")
    public String getMovieListForAdmin() {
        return "adminMovieList";
    }


    @GetMapping("/admin/employee-manager")
    public String getEmployeeList() {
        return "employeeManager";
    }

    @GetMapping("/admin/edit-employee")
    public String getEditEmployee() {
        return "editEmployee";
    }

    @GetMapping("/admin/confirm-ticket")
    public String getConfirmTicket() {
        return "confirmTicket";
    }

    @GetMapping("/admin/ticket-manager")
    public String getBookingList() {
        return "bookingList";
    }

    @GetMapping("/admin/add-employee")
    public String getAddEmployee() {
        return "addEmployee";
    }

    @GetMapping("/admin/promotion-management")
    public String getPromotionManagement() {
        return "promotionManagement";
    }

    @GetMapping("/admin/cinema-room")
    public String getCinemaRoomList() {
        return "cinemaRoom";
    }

    @GetMapping("/admin/schedule-management")
    public String getScheduleManagement() {
        return "scheduleManagement";
    }

    @GetMapping("/all-movie")
    public String findFilmByTime() {
        return "showtime";
    }

    @GetMapping("/test-home")
    public String testHome() {
        return "newHome";
    }

    @GetMapping("/booking-ticket")
    public String getBookingTicket() {
        return "bookTicket";
    }

    @GetMapping("/payment-gateways")
    public String getOnlinePayment() {
        return "paymentGateways";
    }

    @GetMapping("/payment-error")
    public String getPaymentError() {
        return "paymentError";
    }

    @GetMapping("/admin/food-management")
    public String getFoodManagement() {
        return "foodManagement";
    }

    @GetMapping("/movie-rating")
    public String movieRating() {
        return "movieRating";
    }
}
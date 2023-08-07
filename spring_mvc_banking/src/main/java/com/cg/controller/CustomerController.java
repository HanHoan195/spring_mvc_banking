package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Deposits;
import com.cg.model.Transfer;
import com.cg.model.Withdraw;
import com.cg.service.customer.CustomerService;
import com.cg.service.customer.ICustomerService;
import com.cg.service.deposit.IDepositService;
import com.cg.service.transfer.ITransferService;
import com.cg.service.utils.AppUtils;
import com.cg.service.withdraw.IWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    AppUtils appUtils;

    @Autowired
    private ICustomerService customerService = new CustomerService();
    @Autowired
    private IDepositService depositService;
    @Autowired
    private IWithdrawService withdrawService;
    @Autowired
    private ITransferService transferService;

    @GetMapping
    public String showList(Model model) {
        List<Customer> customers = customerService.findAll();

        model.addAttribute("customers", customers);
        return "customer/list";
    }

    @GetMapping("/create")
    public String showCreate(Model model) {
        Customer customer = new Customer();

        model.addAttribute("customer", customer);

        return "customer/create";
    }

    @GetMapping("/{id}")
    public String showUpdate(@PathVariable String id, Model model) {
        try {
            Long customerId = Long.parseLong(id);
            Optional<Customer> customerOptional = customerService.findById(customerId);
            if (customerOptional.isEmpty()) {
                return "redirect:/error/404";
            }
            Customer customer = customerOptional.get();

            model.addAttribute("customer", customer);

            return "customer/update";
        } catch (Exception e) {
            return "error/404";
        }
    }

    @GetMapping("/deposit/{customerId}")
    public String showDeposits(@PathVariable Long customerId, Model model) {

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (customerOptional.isEmpty()) {
            model.addAttribute("error", true);
            model.addAttribute("message", "ID khách hàng khôg tồn tại!");

        } else {
            Customer customer = customerOptional.get();
            Deposits deposits = new Deposits();
            deposits.setCustomer(customer);
            model.addAttribute("deposits", deposits);
        }
        return "customer/deposit";
    }

    @GetMapping("/withdraw/{customerId}")
    public String showWithdraw(@PathVariable Long customerId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (customerOptional.isEmpty()) {
            model.addAttribute("error", true);
            model.addAttribute("message", "ID khách hàng khôg tồn tại!");
        } else {
            Customer customer = customerOptional.get();
            Withdraw withDraw = new Withdraw();
            withDraw.setCustomer(customer);

            redirectAttributes.addFlashAttribute("success", true);
//            redirectAttributes.addFlashAttribute("message", "Rút tiền thành công");

            model.addAttribute("withdraw", withDraw);
        }

        return "customer/withdraw";
    }

    @GetMapping("/transfer/{customerID}")
    public ModelAndView showTransfer(@PathVariable Long customerID) {
        ModelAndView modelAndView = new ModelAndView("customer/transfer");
        Customer sender = customerService.findById(customerID).get();

        List<Customer> recipients = customerService.findAllByIdNot(customerID);

        modelAndView.addObject("sender", sender);
        modelAndView.addObject("recipients", recipients);

        return modelAndView;
    }



    @PostMapping("/{id}")
    public String doUpdate(@PathVariable Long id, @ModelAttribute Customer customer, Model model) {
        customer.setId(id);
        customerService.save(customer);

        List<Customer> customers = customerService.findAll();

        model.addAttribute("customers", customers);
        return "customer/list";
    }


    @PostMapping("create")
    public String doCreate(Model model , @ModelAttribute Customer customer,  BindingResult bindingResult) {
        // bindingResult để hứng lỗi
        new Customer().validate(customer, bindingResult);

        if (bindingResult.hasFieldErrors()) {
            model.addAttribute("hasError", true);
            return "/customer/create";
        }

        customer.setId(null);
        customer.setBalance(BigDecimal.ZERO);

        customerService.save(customer);

        return "redirect:/customers/create";
    }

    @PostMapping("/deposit/{customerId}")
    public String doDeposit(@ModelAttribute Deposits deposits, @PathVariable Long customerId, Model model) {

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (customerOptional.isEmpty()) {
            model.addAttribute("error", true);
            model.addAttribute("message", "ID khách hàng không tồn tại!");

        } else {
            Customer customer = customerOptional.get();

            deposits.setId(null);
            depositService.save(deposits);

            BigDecimal currentBalance = customer.getBalance();
            BigDecimal newBalance = currentBalance.add(deposits.getTransaction_Amount());
            customer.setBalance(newBalance);
            customerService.save(customer);

            deposits.setCustomer(customer);

            model.addAttribute("deposits", deposits);
        }

        return "customer/deposit";
    }

    @PostMapping("/withdraw/{customerId}")
    public String doWithdraw(@ModelAttribute Withdraw withdraw, @PathVariable Long customerId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (customerOptional.isEmpty()) {
            model.addAttribute("error", true);
            model.addAttribute("message", "ID khách hàng khôg tồn tại!");

        } else {
            Customer customer = customerOptional.get();

            withdraw.setId(null);
            withdrawService.save(withdraw);

            BigDecimal currentBalance = customer.getBalance();
            BigDecimal newBalance = currentBalance.subtract(withdraw.getTransaction_Amount());
            customer.setBalance(newBalance);
            customerService.save(customer);

            Withdraw newWithdraw = new Withdraw();
            newWithdraw.setCustomer(customer);

            redirectAttributes.addFlashAttribute("success", true);

            model.addAttribute("withdraw", newWithdraw);
        }
        return "customer/withdraw";
    }

    @PostMapping("/transfer/{customerId}")
    public ModelAndView transfer(@PathVariable Long customerId, @RequestParam("recipientId") long recipientId, @RequestParam("fees") long fees, @RequestParam("transfer") long transferAmount) {
        List<String> errors = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        ModelAndView modelAndView = new ModelAndView("/customer/transfer");
        Customer customerSender = customerService.findById(customerId).get();
        Customer customerRecipient = customerService.findById(recipientId).get();
        if (errors.isEmpty()) {
            long fees_amount = (fees * transferAmount) / 100;
            long transaction_amount = transferAmount + fees_amount;

            if (customerSender.getBalance().compareTo(BigDecimal.valueOf(transaction_amount)) < 0) {
                errors.add("Số dư không đủ" + customerSender.getBalance().subtract(BigDecimal.valueOf(fees_amount)));
            } else {
                BigDecimal balanceSender = customerSender.getBalance().subtract(BigDecimal.valueOf(transaction_amount));
                BigDecimal balanceRecipent = customerRecipient.getBalance().add(BigDecimal.valueOf(transferAmount));
                if (balanceRecipent.toString().length() > 12) {
                    errors.add("Tổng tiền gửi nhỏ hơn 12 chữ số.");
                } else {
                    customerSender.setBalance(balanceSender);
                    customerRecipient.setBalance(balanceRecipent);
                    //BigDecimal fees, BigDecimal fees_amount, BigDecimal transaction_amount, BigDecimal transfer_amount, Long recipient_id, Long sender_id
                    Transfer transfer = new Transfer(BigDecimal.valueOf(fees), BigDecimal.valueOf(fees_amount), BigDecimal.valueOf(transaction_amount), BigDecimal.valueOf(transferAmount), recipientId, customerId);
                    customerService.save(customerSender);
                    customerService.save(customerRecipient);
                    transferService.save(transfer);
                    messages.add("Customer update successfully");
                }
            }
        }
        List<Customer> recipients = customerService.findAllByIdNot(customerId);
        modelAndView.addObject("sender", customerSender);
        modelAndView.addObject("recipients", recipients);
        modelAndView.addObject("messages", messages);
        modelAndView.addObject("errors", errors);
        return modelAndView;
    }

}
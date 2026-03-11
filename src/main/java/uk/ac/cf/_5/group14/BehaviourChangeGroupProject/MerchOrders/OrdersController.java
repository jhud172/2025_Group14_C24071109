package uk.ac.cf._5.group14.BehaviourChangeGroupProject.MerchOrders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrdersController {

    private final MerchOrderService merchOrderService;
    private final AuthHelper authHelper;

    @Autowired
    public OrdersController(MerchOrderService merchOrderService, AuthHelper authHelper) {
        this.merchOrderService = merchOrderService;
        this.authHelper = authHelper;
    }

    @GetMapping
    public ModelAndView getOrders(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(value = "status", required = false, defaultValue = "") String statusFilter,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "created_at") String sortField) {

        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelAndView modelAndView = new ModelAndView("/orders/orders");

        // Get all orders for the user
        List<MerchOrder> allOrders = merchOrderService.getOrdersForUser(user.getId());

        // Filter by search term (order ID or item name)
        List<MerchOrder> filteredOrders = allOrders.stream()
                .filter(order -> search.isEmpty() || 
                        order.getId().toString().contains(search) ||
                        order.getItems().stream()
                            .anyMatch(item -> item.getProductNameSnapshot().toLowerCase().contains(search.toLowerCase())))
                .filter(order -> statusFilter.isEmpty() || order.getShippingStatus().toString().equals(statusFilter))
                .toList();

        // Separate into categories
        List<MerchOrder> activeOrders = filteredOrders.stream()
                .filter(order -> {
                    ShippingStatus status = order.getShippingStatus();
                    return status == ShippingStatus.PENDING || 
                           status == ShippingStatus.PROCESSING || 
                           status == ShippingStatus.SHIPPED ||
                           status == ShippingStatus.OUT_FOR_DELIVERY;
                })
                .toList();

        List<MerchOrder> completedOrders = filteredOrders.stream()
                .filter(order -> order.getShippingStatus() == ShippingStatus.DELIVERED)
                .toList();

        List<MerchOrder> cancelledOrders = filteredOrders.stream()
                .filter(order -> order.getShippingStatus() == ShippingStatus.CANCELLED || 
                                order.getShippingStatus() == ShippingStatus.RETURNED ||
                                order.getShippingStatus() == ShippingStatus.FAILED_DELIVERY)
                .toList();

        modelAndView.addObject("user", user);
        modelAndView.addObject("search", search);
        modelAndView.addObject("statusFilter", statusFilter);
        modelAndView.addObject("activeOrders", activeOrders);
        modelAndView.addObject("completedOrders", completedOrders);
        modelAndView.addObject("cancelledOrders", cancelledOrders);
        modelAndView.addObject("shippingStatuses", ShippingStatus.values());

        return modelAndView;
    }
}

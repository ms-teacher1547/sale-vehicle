package com.tpinf4067.sale_vehicle.service;

import com.tpinf4067.sale_vehicle.domain.Cart;
import com.tpinf4067.sale_vehicle.domain.CartItem;
import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.patterns.auth.User;
import com.tpinf4067.sale_vehicle.patterns.customer.Customer;
import com.tpinf4067.sale_vehicle.patterns.document.*;
import com.tpinf4067.sale_vehicle.patterns.order.factory.*;
import com.tpinf4067.sale_vehicle.patterns.order.state.*;
import com.tpinf4067.sale_vehicle.patterns.payment.PaymentStatus;
import com.tpinf4067.sale_vehicle.patterns.order.observer.EmailOrderNotifier;
import com.tpinf4067.sale_vehicle.patterns.order.observer.OrderNotifier;
import com.tpinf4067.sale_vehicle.repository.CustomerRepository;
import com.tpinf4067.sale_vehicle.repository.OrderRepository;
import com.tpinf4067.sale_vehicle.repository.PaymentRepository;
import com.tpinf4067.sale_vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final PDFDocumentAdapter pdfAdapter;
    private final OrderNotifier orderNotifier;
    private final CartService cartService;
    private final PaymentRepository paymentRepository;

    public OrderService(OrderRepository orderRepository, VehicleRepository vehicleRepository,
                        CustomerRepository customerRepository, CartService cartService, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
        this.pdfAdapter = new PDFDocumentAdapter();
        this.orderNotifier = new OrderNotifier();
        this.cartService = cartService;
        this.paymentRepository = paymentRepository;
    }

    // ✅ Création d'une commande depuis le panier SANS générer les documents
    public Order createOrderFromCart(Long customerId) {
        Cart cart = cartService.getCartForCustomer(customerId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Le panier est vide, impossible de passer une commande !");
        }

        // ✅ Vérification : Le client ne peut pas passer une nouvelle commande s'il en a une en attente de paiement
        boolean hasUnpaidOrder = orderRepository.findByCustomerId(customerId)
                .stream()
                .anyMatch(order -> !paymentRepository.existsByOrderAndStatus(order, PaymentStatus.PAYE));

        if (hasUnpaidOrder) {
            throw new IllegalStateException("❌ Vous avez une commande en attente de paiement. Veuillez d'abord la payer avant d'en créer une nouvelle.");
        }

        // Vérification du type de paiement
        // PaymentType paymentType;
        // try {
        //     paymentType = PaymentType.valueOf(paymentTypeStr.toUpperCase());
        // } catch (IllegalArgumentException e) {
        //     throw new IllegalArgumentException("Type de paiement invalide : " + paymentTypeStr);
        // } 

        //Factory Method pour créer la commande
        OrderFactory orderFactory = //(paymentType == PaymentType.COMPTANT) ? 
        //                             new ComptantOrderFactory() : 
                                    new CreditOrderFactory();

        Order order = orderFactory.createOrder(cart);//, paymentType);
        order.setCustomer(cart.getCustomer());
        order.setState(new PendingState()); // ✅ Commande en attente
        order.setDateDeCommande(new Date());

        for (CartItem item : cart.getItems()) {
            order.addVehicleWithOptions(item.getVehicle(), item.getOptions(), item.getQuantity());

            Vehicle vehicle = item.getVehicle();
            if (vehicle.getStockQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Le véhicule " + vehicle.getName() + " n'a pas assez de stock !");
            }
            vehicle.setStockQuantity(vehicle.getStockQuantity() - item.getQuantity());
            vehicleRepository.save(vehicle);
        }

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(customerId);
        
        // 🔥 **Ne pas générer les documents immédiatement**  
        orderNotifier.addObserver(new EmailOrderNotifier(cart.getCustomer().getEmail()));
        orderNotifier.notifyObservers("Votre commande est créée et en attente de confirmation.");
        
        return savedOrder;
    }

    // ✅ Génération des documents UNIQUEMENT après confirmation
    private void generateOrderDocuments(Order order) {
        String orderId = String.valueOf(order.getId()); // ✅ ID unique pour chaque commande

        System.out.println("📄 Construction des documents pour la commande #" + orderId);

        // 🔥 Réinitialisation des documents pour éviter l'accumulation d'anciens fichiers
        DocumentLiasseSingleton.getInstance().clearDocuments();

        OrderDocumentBuilder builder = new OrderDocumentBuilder();
        builder.constructOrderDocuments(order);

        // ✅ Vérifier si des documents ont été créés
        List<Document> documents = DocumentLiasseSingleton.getInstance().getDocuments();
        if (documents.isEmpty()) {
            System.out.println("⚠️ Aucun document créé pour la commande #" + orderId);
            return;
        }

        // 🔥 Associer les documents à la commande et les sauvegarder
        for (Document document : documents) {
            String formattedTitle = document.getTitle() + " - Commande #" + orderId;
            String fileName = formattedTitle.replace(" ", "_").replace("'", "") + ".pdf"; // ✅ Nettoyage du nom de fichier

            document.setTitle(formattedTitle);
            document.setFilename(fileName); // ✅ Ajoute le nom du fichier
            document.setOrder(order); // ✅ Associe le document à la commande

            pdfAdapter.export(document);
            order.getDocuments().add(document);
        }

        orderRepository.save(order); // ✅ Sauvegarde en base avec les documents
        System.out.println("✅ Tous les documents ont été générés et stockés en base pour la commande #" + orderId);
    }







    // ✅ Confirmation d'une commande et génération des documents
    public Order confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));

        if (!(order.getState() instanceof PendingState)) {
            throw new IllegalStateException("Seules les commandes en cours peuvent être confirmées.");
        }

        // 🔥 Changer l'état en "VALIDEE"
        order.setState(new ValidatedState());
        orderRepository.save(order);

        // ✅ Ajouter un log avant la génération des documents
        System.out.println("Début de la génération des documents pour la commande #" + orderId);

        // 🔥 Générer les documents maintenant
        generateOrderDocuments(order);

        orderNotifier.notifyObservers("Votre commande est maintenant VALIDEE.");
        
        // ✅ Ajouter un log après la génération des documents
        System.out.println("✅ Documents générés pour la commande #" + orderId);
        
        return order;
    }



    // ✅ Changer l'état d'une commande avec State Pattern
    public Order changeOrderStatus(Long orderId, boolean next) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));
    
        String previousState = order.getState().getStatus();
    
        // ❌ Empêcher le retour en arrière si l'état est "VALIDEE"
        if (!next && "VALIDEE".equals(order.getState().getStatus())) {
            throw new IllegalStateException("Impossible de revenir à un état précédent après validation !");
        }
    
        // ✅ Changer d'état
        if (next) {
            order.nextState();
        } else {
            order.previousState();
        }
    
        orderRepository.save(order);
    
        // ✅ Générer les documents uniquement si on passe à "VALIDEE"
        if ("VALIDEE".equals(order.getState().getStatus()) && !"VALIDEE".equals(previousState)) {
            System.out.println("📄 Génération des documents car la commande est VALIDEE !");
            generateOrderDocuments(order);
        }
    
        orderNotifier.notifyObservers("Votre commande est maintenant : " + order.getState().getStatus());
        return order;
    }
    
    

    // ✅ Récupérer toutes les commandes
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // ✅ Rechercher des commandes par client et/ou état
    public List<Order> searchOrders(Long customerId, String state) {
        List<Order> orders;
        
        if (customerId != null) {
            orders = orderRepository.findByCustomerId(customerId);
        } else {
            orders = orderRepository.findAll();
        }
    
        // 🔥 Filtrage manuel sur l'état car `state` est @Transient et non en base de données
        if (state != null) {
            orders = orders.stream()
                    .filter(o -> o.getState().getStatus().equalsIgnoreCase(state))
                    .toList();
        }
    
        return orders;
    }

    public Order changeLastOrderStatusForUser(User user, boolean next) {
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new IllegalStateException("Utilisateur sans client associé !");
        }

        // 🔥 Récupérer la dernière commande du client
        List<Order> orders = orderRepository.findByCustomerId(customer.getId());

        if (orders.isEmpty()) {
            throw new IllegalStateException("Aucune commande trouvée !");
        }

        // 🏆 Dernière commande
        Order lastOrder = orders.get(orders.size() - 1);

        // 🔥 Changer son état
        return changeOrderStatus(lastOrder.getId(), next);
    }

    // ✅ Récupérer toutes les commandes d'un client spécifique
    public List<Order> getOrdersByCustomer(User user) {
        if (user.getCustomer() == null) {
            throw new IllegalStateException("Utilisateur sans client associé !");
        }

        return orderRepository.findByCustomerId(user.getCustomer().getId());
    }


}

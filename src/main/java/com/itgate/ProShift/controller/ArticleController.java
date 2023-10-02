package com.itgate.ProShift.controller;

import com.itgate.ProShift.entity.Article;
import com.itgate.ProShift.entity.User;
import com.itgate.ProShift.repository.ArticleRepository;
import com.itgate.ProShift.repository.UserRepository;
import com.itgate.ProShift.service.ArticleService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import com.stripe.param.TokenCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private String secretKey="sk_test_51Nvib0CisuJiaFZAl8Kq0ERK7yf7LzBzW4ZrnfSiqePHDyiTAM83WJ5ul1KqGuhPkLRhiMGl5nbrKRcexarJAm21002LYRRqLE";

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin/findAll")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok().body( articleService.getAll());

    }
    @PostMapping("/admin/create")
    public ResponseEntity<?> create(@RequestBody Article article){
        return ResponseEntity.ok().body( articleService.create(article));

    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Article article){
        Article updatedArticle = articleService.update(id, article);
        if (updatedArticle != null) {
            return ResponseEntity.ok().body(updatedArticle);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        articleService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/findById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        Article article = articleService.getById(id);
        if (article != null) {
            return ResponseEntity.ok().body(article);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/user/{userId}/purchase-article/{articleId}")
    public ResponseEntity<?> createPayment(@PathVariable Long userId,
                                @PathVariable Long articleId) throws StripeException {
        Stripe.apiKey = "sk_test_51Nvib0CisuJiaFZAl8Kq0ERK7yf7LzBzW4ZrnfSiqePHDyiTAM83WJ5ul1KqGuhPkLRhiMGl5nbrKRcexarJAm21002LYRRqLE";
        // Retrieve the user and article based on userId and articleId
        User user = userRepository.findById(userId).orElse(null);
        Article article = articleRepository.findById(articleId).orElse(null);
        if (user==null){
            return ResponseEntity.badRequest().body("User does not exists");
        }
        if (article==null){
            return ResponseEntity.badRequest().body("User does not exists");
        }
        if (article.isFreemium()){
            return ResponseEntity.badRequest().body("The article youre trying to purchase is freemium");
        }
        if(user.isFreemium()==false){
            return ResponseEntity.badRequest().body("User is already premium and have access to this article");
        }
        // Create a token using card details
      /*  TokenCreateParams paramss = TokenCreateParams.builder()
                .setCard(TokenCreateParams.Card.builder()
                        .setNumber("4242424242424242")
                        .setExpMonth("12")
                        .setExpYear("2023")
                        .setCvc("123")
                        .build())
                .build();*/
        /*Map<String, Object> card = new HashMap<>();
        card.put("number", "4242424242424242");
        card.put("exp_month", 9);
        card.put("exp_year", 2024);
        card.put("cvc", "314");
        Map<String, Object> paramss = new HashMap<>();
        paramss.put("card", card);

        Token token = Token.create(paramss);*/
        Map<String, Object> params = new HashMap<>();
        params.put("amount", (int)article.getPrice());
        params.put("currency", "usd");
        params.put("source", "tok_amex");
        params.put(
                "description",
                "user : "+user.getEmail()+ "has purchased article " + article.getTitre()
        );

        Charge charge = Charge.create(params);
        //nzid message eli howa chree!
        return ResponseEntity.ok().body(article);
    }
    @PostMapping("/api/purchase-user/{userId}")
    public ResponseEntity<?> createPaymentForPremium(@PathVariable Long userId) throws StripeException {
        Stripe.apiKey = "sk_test_51Nvib0CisuJiaFZAl8Kq0ERK7yf7LzBzW4ZrnfSiqePHDyiTAM83WJ5ul1KqGuhPkLRhiMGl5nbrKRcexarJAm21002LYRRqLE";
        // Retrieve the user  based on userId
        User user = userRepository.findById(userId).orElse(null);
        if (user==null){
            return ResponseEntity.badRequest().body("User does not exists");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("amount", 500);
        params.put("currency", "usd");
        params.put("source", "tok_amex");
        params.put(
                "description",
                "user : "+user.getEmail()+ "has purchased premium pass "
        );
        if (user.isFreemium()==false){
            return ResponseEntity.badRequest().body("User is already premium");
        }
        Charge charge = Charge.create(params);
        user.setFreemium(false);
        userRepository.save(user);
        return ResponseEntity.ok().body("Operation Success, user granted premium access");
    }


}

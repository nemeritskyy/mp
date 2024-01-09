package ua.com.obox.dbschema.restaurant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.com.obox.dbschema.allergen.AllergenRepository;
import ua.com.obox.dbschema.attachment.AttachmentRepository;
import ua.com.obox.dbschema.category.Category;
import ua.com.obox.dbschema.category.CategoryResponse;
import ua.com.obox.dbschema.dish.Dish;
import ua.com.obox.dbschema.dish.DishResponse;
import ua.com.obox.dbschema.language.Language;
import ua.com.obox.dbschema.language.LanguageRepository;
import ua.com.obox.dbschema.language.SelectedLanguage;
import ua.com.obox.dbschema.language.SelectedLanguageRepository;
import ua.com.obox.dbschema.mark.MarkRepository;
import ua.com.obox.dbschema.sorting.EntityOrder;
import ua.com.obox.dbschema.sorting.EntityOrderRepository;
import ua.com.obox.dbschema.tools.BasicAllergensAndMarks;
import ua.com.obox.dbschema.tools.FieldUpdateFunction;
import ua.com.obox.dbschema.menu.Menu;
import ua.com.obox.dbschema.menu.MenuRepository;
import ua.com.obox.dbschema.menu.MenuResponse;
import ua.com.obox.dbschema.tenant.Tenant;
import ua.com.obox.dbschema.tenant.TenantRepository;
import ua.com.obox.dbschema.tools.Validator;
import ua.com.obox.dbschema.tools.attachment.AttachmentTools;
import ua.com.obox.dbschema.tools.exception.ExceptionTools;
import ua.com.obox.dbschema.tools.logging.LogLevel;
import ua.com.obox.dbschema.tools.logging.LoggingService;
import ua.com.obox.dbschema.tools.response.BadFieldsResponse;
import ua.com.obox.dbschema.tools.response.ResponseErrorMap;
import ua.com.obox.dbschema.tools.services.UpdateServiceHelper;
import ua.com.obox.dbschema.tools.translation.CheckHeader;
import ua.com.obox.dbschema.translation.Translation;
import ua.com.obox.dbschema.translation.TranslationRepository;
import ua.com.obox.dbschema.translation.assistant.CreateTranslation;
import ua.com.obox.dbschema.translation.responsebody.Content;
import ua.com.obox.dbschema.translation.responsebody.MenuTranslationEntry;
import ua.com.obox.dbschema.translation.responsebody.RestaurantTranslationEntry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    @PersistenceContext
    private EntityManager entityManager;
    private final RestaurantRepository restaurantRepository;
    private final TenantRepository tenantRepository;
    private final MenuRepository menuRepository;
    private final EntityOrderRepository entityOrderRepository;
    private final TranslationRepository translationRepository;
    private final MarkRepository markRepository;
    private final AllergenRepository allergenRepository;
    private final AttachmentRepository attachmentRepository;
    private final LoggingService loggingService;
    private final UpdateServiceHelper serviceHelper;
    private final LanguageRepository languageRepository;
    private final SelectedLanguageRepository selectedLanguageRepository;
    private final ResourceBundle translation = ResourceBundle.getBundle("translation.messages");
    @Value("${application.image-dns}")
    private String attachmentsDns;

    public List<MenuResponse> getAllMenusByRestaurantId(String restaurantId, String acceptLanguage) {
        String finalAcceptLanguage = CheckHeader.checkHeaderLanguage(acceptLanguage);
        ObjectMapper objectMapper = new ObjectMapper();
        AtomicReference<Content<MenuTranslationEntry>> content = new AtomicReference<>();
        AtomicReference<Translation> translation = new AtomicReference<>();

        restaurantRepository.findByRestaurantId(restaurantId).orElseThrow(() -> ExceptionTools.notFoundException(".restaurantNotFound", finalAcceptLanguage, restaurantId));

        List<Menu> menus = menuRepository.findAllByRestaurant_RestaurantIdOrderByCreatedAt(restaurantId);

        // for sorting results
        EntityOrder sortingExist = entityOrderRepository.findByReferenceIdAndReferenceType(restaurantId, "restaurant").orElse(null);
        if (sortingExist != null) {
            List<String> MenuIdsInOrder = Arrays.stream(sortingExist.getSortedList().split(",")).toList();
            menus.sort(Comparator.comparingInt(menu -> {
                int index = MenuIdsInOrder.indexOf(menu.getMenuId());
                return index != -1 ? index : Integer.MAX_VALUE;
            }));
        }

        return menus.stream()
                .map(menu -> {
                    try {
                        translation.set(translationRepository.findAllByTranslationId(menu.getTranslationId()).orElseThrow(() ->
                                ExceptionTools.notFoundException(".translationNotFound", finalAcceptLanguage, menu.getMenuId())));
                        content.set(objectMapper.readValue(translation.get().getContent(), new TypeReference<>() {
                        }));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    return MenuResponse.builder()
                            .restaurantId(menu.getRestaurant().getRestaurantId())
                            .menuId(menu.getMenuId())
                            .originalLanguage(menu.getOriginalLanguage())
                            .translationId(menu.getTranslationId())
                            .state(menu.getState())
                            .content(content.get())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public RestaurantResponse getRestaurantById(String restaurantId, String acceptLanguage) throws JsonProcessingException {
        String finalAcceptLanguage = CheckHeader.checkHeaderLanguage(acceptLanguage);

        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId).orElseThrow(() -> ExceptionTools.notFoundException(".restaurantNotFound", finalAcceptLanguage, restaurantId));
        Translation translation = translationRepository.findAllByTranslationId(restaurant.getTranslationId())
                .orElseThrow(() -> ExceptionTools.notFoundException(".translationNotFound", finalAcceptLanguage, restaurantId));

        ObjectMapper objectMapper = new ObjectMapper();
        Content<RestaurantTranslationEntry> content = objectMapper.readValue(translation.getContent(), new TypeReference<>() {
        });

        return RestaurantResponse.builder()
                .restaurantId(restaurant.getRestaurantId())
                .tenantId(restaurant.getTenant().getTenantId())
                .originalLanguage(restaurant.getOriginalLanguage())
                .translationId(restaurant.getTranslationId())
                .content(content)
                .build();
    }

    public RestaurantResponseId createRestaurant(Restaurant request, String acceptLanguage) throws JsonProcessingException {
        String finalAcceptLanguage = CheckHeader.checkHeaderLanguage(acceptLanguage);
        Map<String, String> fieldErrors = new ResponseErrorMap<>();

        request.setTenantIdForRestaurant(request.getTenantId());

        Optional<Tenant> tenant = tenantRepository.findByTenantId(request.getTenantId());
        if (tenant.isEmpty())
            fieldErrors.put("tenant_id", String.format(translation.getString(finalAcceptLanguage + ".tenantNotFound"), request.getTenant().getTenantId()));


        Restaurant restaurant = Restaurant.builder()
                .tenant(tenant.orElse(null))
                .build();

        validateRequest(request, restaurant, finalAcceptLanguage, fieldErrors, true);

        restaurant.setOriginalLanguage(request.getLanguage());
        restaurant.setCreatedAt(Instant.now().getEpochSecond());
        restaurant.setUpdatedAt(Instant.now().getEpochSecond());
        restaurantRepository.save(restaurant);

        {
            CreateTranslation<RestaurantTranslationEntry> createTranslation = new CreateTranslation<>(translationRepository);
            RestaurantTranslationEntry entry = new RestaurantTranslationEntry(restaurant.getName(), restaurant.getAddress());
            Translation translation = createTranslation
                    .create(restaurant.getRestaurantId(), "restaurant", request.getLanguage(), entry);
            restaurant.setTranslationId(translation.getTranslationId());
        }

        try {
            BasicAllergensAndMarks.addBasicMarks(restaurant.getRestaurantId(), translationRepository, markRepository);
            BasicAllergensAndMarks.addBasicAllergens(restaurant.getRestaurantId(), translationRepository, allergenRepository);
        } catch (Exception ex) {
            loggingService.log(LogLevel.ERROR, "Problem with adding basic marks or allergens");
        }

        Optional<Language> language = languageRepository.findByName("uk-UA"); // by default
        if (language.isPresent()) {
            SelectedLanguage selectedLanguage = SelectedLanguage.builder()
                    .restaurantId(restaurant.getRestaurantId())
                    .languageId(language.get().getLanguageId())
                    .createdAt(Instant.now().getEpochSecond())
                    .updatedAt(Instant.now().getEpochSecond())
                    .build();
            selectedLanguageRepository.save(selectedLanguage);
        }

        return RestaurantResponseId.builder()
                .restaurantId(restaurant.getRestaurantId())
                .build();
    }

    public void patchRestaurantById(String restaurantId, Restaurant request, String acceptLanguage) throws JsonProcessingException {
        String finalAcceptLanguage = CheckHeader.checkHeaderLanguage(acceptLanguage);
        Map<String, String> fieldErrors = new ResponseErrorMap<>();

        try (Session session = entityManager.unwrap(Session.class)) {
            Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId).orElseThrow(() -> ExceptionTools.notFoundException(".restaurantNotFound", finalAcceptLanguage, restaurantId));
            Translation translation = translationRepository.findAllByTranslationId(restaurant.getTranslationId())
                    .orElseThrow(() -> ExceptionTools.notFoundException(".translationNotFound", finalAcceptLanguage, restaurantId));

            session.evict(restaurant); // unbind the session

            validateRequest(request, restaurant, finalAcceptLanguage, fieldErrors, false);
            updateTranslation(restaurant, request.getLanguage(), translation);

            restaurant.setUpdatedAt(Instant.now().getEpochSecond());
            restaurantRepository.save(restaurant);
        }
    }

    public void deleteRestaurantById(String restaurantId, String acceptLanguage) {
        String finalAcceptLanguage = CheckHeader.checkHeaderLanguage(acceptLanguage);

        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId).orElseThrow(() -> ExceptionTools.notFoundException(".restaurantNotFound", finalAcceptLanguage, restaurantId));

        restaurantRepository.delete(restaurant);
    }

    public List<MenuResponse> getAllMenusCategoriesDishesByRestaurantId(String restaurantId, String acceptLanguage) {
        String finalAcceptLanguage = CheckHeader.checkHeaderLanguage(acceptLanguage);

        ObjectMapper objectMapper = new ObjectMapper();
        AtomicReference<Content> content = new AtomicReference<>();
        AtomicReference<Translation> translation = new AtomicReference<>();

        restaurantRepository.findByRestaurantId(restaurantId).orElseThrow(() -> ExceptionTools.notFoundException(".restaurantNotFound", finalAcceptLanguage, restaurantId));

        List<Menu> menus = menuRepository.findAllByRestaurant_RestaurantIdOrderByCreatedAt(restaurantId);

        // for sorting results
        EntityOrder sortingExist = entityOrderRepository.findByReferenceIdAndReferenceType(restaurantId, "restaurant").orElse(null);
        if (sortingExist != null) {
            List<String> MenuIdsInOrder = Arrays.stream(sortingExist.getSortedList().split(",")).toList();
            menus.sort(Comparator.comparingInt(menu -> {
                int index = MenuIdsInOrder.indexOf(menu.getMenuId());
                return index != -1 ? index : Integer.MAX_VALUE;
            }));
        }

        return menus.stream()
                .map(menu -> {
                    try {
                        translation.set(translationRepository.findAllByTranslationId(menu.getTranslationId()).orElseThrow(() ->
                                ExceptionTools.notFoundException(".translationNotFound", finalAcceptLanguage, menu.getMenuId())));
                        content.set(objectMapper.readValue(translation.get().getContent(), new TypeReference<>() {
                        }));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    MenuResponse menuResponse = new MenuResponse();
                    menuResponse.setRestaurantId(menu.getRestaurant().getRestaurantId());
                    menuResponse.setMenuId(menu.getMenuId());
                    menuResponse.setOriginalLanguage(menu.getOriginalLanguage());
                    menuResponse.setTranslationId(menu.getTranslationId());
                    menuResponse.setState(menu.getState());
                    menuResponse.setContent(content.get());

                    //start menu sorting
                    EntityOrder categoryExist = entityOrderRepository.findByReferenceIdAndReferenceType(menu.getMenuId(), "menu").orElse(null);
                    List<String> CategoryIdsInOrder = new ArrayList<>();
                    if (categoryExist != null) {
                        CategoryIdsInOrder = Arrays.stream(categoryExist.getSortedList().split(",")).toList();
                    }
                    List<String> finalCategoryIdsInOrder = CategoryIdsInOrder;
                    menu.getCategories().sort(Comparator.comparing(Category::getCreatedAt).reversed());
                    menu.getCategories().sort(Comparator.comparingInt(category -> {
                        int index = finalCategoryIdsInOrder.indexOf(category.getCategoryId());
                        return index != -1 ? index : Integer.MAX_VALUE;
                    }));
                    //finish menu sorting

                    List<CategoryResponse> categoryResponseList = menu.getCategories().stream()
                            .map(category -> {
                                try {
                                    translation.set(translationRepository.findAllByTranslationId(category.getTranslationId()).orElseThrow(() ->
                                            ExceptionTools.notFoundException(".translationNotFound", finalAcceptLanguage, category.getCategoryId())));
                                    content.set(objectMapper.readValue(translation.get().getContent(), new TypeReference<>() {
                                    }));
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }

                                CategoryResponse categoryResponse = CategoryResponse.builder()
                                        .menuId(category.getMenu().getMenuId())
                                        .categoryId(category.getCategoryId())
                                        .originalLanguage(category.getOriginalLanguage())
                                        .translationId(category.getTranslationId())
                                        .state(category.getState())
                                        .content(content.get())
                                        .build();

                                //start dish sorting
                                EntityOrder dishExist = entityOrderRepository.findByReferenceIdAndReferenceType(category.getCategoryId(), "category").orElse(null);
                                List<String> DishIdsInOrder = new ArrayList<>();
                                if (dishExist != null) {
                                    DishIdsInOrder = Arrays.stream(dishExist.getSortedList().split(",")).toList();
                                }
                                List<String> finalDishIdsInOrder = DishIdsInOrder;
                                category.getDishes().sort(Comparator.comparing(Dish::getCreatedAt).reversed());
                                category.getDishes().sort(Comparator.comparingInt(dish -> {
                                    int index = finalDishIdsInOrder.indexOf(dish.getDishId());
                                    return index != -1 ? index : Integer.MAX_VALUE;
                                }));
                                //finish dish sorting

                                List<DishResponse> dishResponseList = category.getDishes().stream()
                                        .map(dish -> {
                                            try {
                                                translation.set(translationRepository.findAllByTranslationId(dish.getTranslationId()).orElseThrow(() ->
                                                        ExceptionTools.notFoundException(".translationNotFound", finalAcceptLanguage, dish.getDishId())));
                                                content.set(objectMapper.readValue(translation.get().getContent(), new TypeReference<>() {
                                                }));
                                            } catch (JsonProcessingException e) {
                                                throw new RuntimeException(e);
                                            }

                                            return DishResponse.builder()
                                                    .categoryId(dish.getCategory().getCategoryId())
                                                    .dishId(dish.getDishId())
                                                    .originalLanguage(dish.getOriginalLanguage())
                                                    .translationId(dish.getTranslationId())
                                                    .price(dish.getPrice())
                                                    .specialPrice(dish.getSpecialPrice())
                                                    .cookingTime(dish.getCookingTime())
                                                    .calories(dish.getCalories())
                                                    .weight(dish.getWeight())
                                                    .weightUnit(dish.getWeightUnit())
                                                    .inStock(dish.getInStock())
                                                    .state(dish.getState())
                                                    .allergens(dish.getAllergens() == null ? null : Arrays.stream(dish.getAllergens().split(",")).toList())
                                                    .marks(dish.getMarks() == null ? null : Arrays.stream(dish.getMarks().split(",")).toList())
                                                    .image(AttachmentTools.getURL(dish, attachmentRepository, attachmentsDns))
                                                    .content(content.get())
                                                    .build();
                                        }).collect(Collectors.toList());

                                categoryResponse.setDishes(dishResponseList);
                                return categoryResponse;
                            }).collect(Collectors.toList());

                    menuResponse.setCategories(categoryResponseList);
                    return menuResponse;

                }).collect(Collectors.toList());
    }

    private void validateRequest(Restaurant request, Restaurant restaurant, String finalAcceptLanguage, Map<String, String> fieldErrors, boolean required) {
        fieldErrors.put("language", Validator.validateLanguage(request.getLanguage(), finalAcceptLanguage));
        updateField(request.getName(), required, fieldErrors, "name",
                (name) -> serviceHelper.updateNameField(restaurant::setName, name, finalAcceptLanguage));
        fieldErrors.put("address", serviceHelper.updateVarcharField(restaurant::setAddress, request.getAddress(), "address", finalAcceptLanguage));

        if (fieldErrors.size() > 0)
            throw new BadFieldsResponse(HttpStatus.BAD_REQUEST, fieldErrors);
    }

    private <T> void updateField(T value, boolean required, Map<String, String> fieldErrors, String fieldName, FieldUpdateFunction<T> updateFunction) {
        if (value != null || required) {
            String error = updateFunction.updateField(value);
            if (error != null) {
                fieldErrors.put(fieldName, error);
            }
        }
    }

    private void updateTranslation(Restaurant restaurant, String language, Translation translation) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<Content<RestaurantTranslationEntry>> typeReference = new TypeReference<>() {
        };
        Content<RestaurantTranslationEntry> content = objectMapper.readValue(translation.getContent(), typeReference);
        Map<String, RestaurantTranslationEntry> languagesMap = content.getContent();
        if (languagesMap.get(language) != null) {
            if (restaurant.getName() == null)
                restaurant.setName(languagesMap.get(language).getName());
            if (restaurant.getAddress() == null) {
                restaurant.setAddress(content.getContent().get(language).getAddress());
            } else if (restaurant.getAddress().equals("")) {
                restaurant.setAddress(null);
            }
        }
        languagesMap.put(language, new RestaurantTranslationEntry(restaurant.getName(), restaurant.getAddress()));
        translation.setContent(objectMapper.writeValueAsString(content));
        translation.setUpdatedAt(Instant.now().getEpochSecond());
    }
}
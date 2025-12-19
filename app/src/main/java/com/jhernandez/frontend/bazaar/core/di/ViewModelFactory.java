package com.jhernandez.frontend.bazaar.core.di;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.jhernandez.frontend.bazaar.core.file.FileProviderService;
import com.jhernandez.frontend.bazaar.core.file.FileUtils;
import com.jhernandez.frontend.bazaar.data.api.ApiService;
import com.jhernandez.frontend.bazaar.data.network.SessionManager;
import com.jhernandez.frontend.bazaar.data.repository.BackupRepository;
import com.jhernandez.frontend.bazaar.data.repository.CartRepository;
import com.jhernandez.frontend.bazaar.data.repository.CategoryRepository;
import com.jhernandez.frontend.bazaar.data.repository.MessageRepository;
import com.jhernandez.frontend.bazaar.data.repository.OrderRepository;
import com.jhernandez.frontend.bazaar.data.repository.OrderStatusRepository;
import com.jhernandez.frontend.bazaar.data.repository.PaymentRepository;
import com.jhernandez.frontend.bazaar.data.repository.PreferencesRepository;
import com.jhernandez.frontend.bazaar.data.repository.ProductRepository;
import com.jhernandez.frontend.bazaar.data.repository.ReviewRepository;
import com.jhernandez.frontend.bazaar.data.repository.SessionRepository;
import com.jhernandez.frontend.bazaar.data.repository.UserRepository;
import com.jhernandez.frontend.bazaar.data.repository.UserRoleRepository;
import com.jhernandez.frontend.bazaar.domain.port.BackupRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.CartRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.CategoryRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.MessageRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.OrderRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.OrderStatusRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.PaymentRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.PreferencesRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.ProductRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.ReviewRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.SessionRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.UserRepositoryPort;
import com.jhernandez.frontend.bazaar.domain.port.UserRoleRepositoryPort;
import com.jhernandez.frontend.bazaar.ui.auth.LoginViewModel;
import com.jhernandez.frontend.bazaar.ui.backup.admin.BackupViewModel;
import com.jhernandez.frontend.bazaar.ui.backup.detail.BackupDetailViewModel;
import com.jhernandez.frontend.bazaar.ui.cart.CartViewModel;
import com.jhernandez.frontend.bazaar.ui.category.admin.AdminCategoriesViewModel;
import com.jhernandez.frontend.bazaar.ui.category.manage.ManageCategoryViewModel;
import com.jhernandez.frontend.bazaar.ui.home.HomeViewModel;
import com.jhernandez.frontend.bazaar.ui.launcher.LauncherViewModel;
import com.jhernandez.frontend.bazaar.ui.main.MainViewModel;
import com.jhernandez.frontend.bazaar.ui.messages.user.UserMessagesViewModel;
import com.jhernandez.frontend.bazaar.ui.messages.detail.MessageDetailViewModel;
import com.jhernandez.frontend.bazaar.ui.order.purchase.customer.PurchaseOrdersViewModel;
import com.jhernandez.frontend.bazaar.ui.order.purchase.detail.PurchaseOrderDetailViewModel;
import com.jhernandez.frontend.bazaar.ui.order.sale.detail.SaleOrderDetailViewModel;
import com.jhernandez.frontend.bazaar.ui.order.sale.shop.SaleOrdersViewModel;
import com.jhernandez.frontend.bazaar.ui.payment.PaymentViewModel;
import com.jhernandez.frontend.bazaar.ui.product.admin.AdminProductsViewModel;
import com.jhernandez.frontend.bazaar.ui.product.detail.ProductDetailViewModel;
import com.jhernandez.frontend.bazaar.ui.product.favourite.FavouriteProductsViewModel;
import com.jhernandez.frontend.bazaar.ui.product.manage.ManageProductViewModel;
import com.jhernandez.frontend.bazaar.ui.product.search.SearchProductViewModel;
import com.jhernandez.frontend.bazaar.ui.product.shop.ShopProductsViewModel;
import com.jhernandez.frontend.bazaar.ui.review.detail.ReviewDetailViewModel;
import com.jhernandez.frontend.bazaar.ui.review.manage.ManageReviewViewModel;
import com.jhernandez.frontend.bazaar.ui.review.product.ProductReviewsViewModel;
import com.jhernandez.frontend.bazaar.ui.review.user.UserReviewsViewModel;
import com.jhernandez.frontend.bazaar.ui.user.account.AccountViewModel;
import com.jhernandez.frontend.bazaar.ui.user.admin.AdminUsersViewModel;
import com.jhernandez.frontend.bazaar.ui.user.manage.ManageUserViewModel;
import com.jhernandez.frontend.bazaar.ui.user.profile.ProfileViewModel;

/*
 * Factory class to create ViewModel instances with required dependencies.
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final SessionRepositoryPort sessionRepository;
    private final UserRepositoryPort userRepository;
    private final UserRoleRepositoryPort userRoleRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final ProductRepositoryPort productRepository;
    private final FileProviderService fileProviderService;
    private final CartRepositoryPort cartRepository;
    private final OrderRepositoryPort orderRepository;
    private final OrderStatusRepositoryPort orderStatusRepository;
    private final ReviewRepositoryPort reviewRepository;
    private final PreferencesRepositoryPort preferencesRepository;
    private final PaymentRepositoryPort paymentRepository;
    private final BackupRepositoryPort backupRepository;
    private final MessageRepositoryPort messageRepository;

    public ViewModelFactory(Application application) {
        SessionManager sessionManager = SessionManager.getInstance(application.getApplicationContext());
        ApiService apiService = sessionManager.getApiService();
        this.fileProviderService = new FileUtils(application.getApplicationContext());
        this.sessionRepository = new SessionRepository(sessionManager);
        this.userRepository = new UserRepository(apiService);
        this.userRoleRepository = new UserRoleRepository(apiService);
        this.categoryRepository = new CategoryRepository(apiService);
        this.productRepository = new ProductRepository(apiService);
        this.cartRepository = new CartRepository(apiService);
        this.orderRepository = new OrderRepository(apiService);
        this.orderStatusRepository = new OrderStatusRepository(apiService);
        this.reviewRepository = new ReviewRepository(apiService);
        this.preferencesRepository = new PreferencesRepository(apiService);
        this.paymentRepository = new PaymentRepository(apiService);
        this.backupRepository = new BackupRepository(apiService);
        this.messageRepository = new MessageRepository(apiService);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(LauncherViewModel.class)) {
            return (T) new LauncherViewModel(sessionRepository, userRepository);
        }

        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(sessionRepository, messageRepository);
        }

        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(sessionRepository, userRepository);
        }

        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(sessionRepository);
        }

        if (modelClass.isAssignableFrom(AdminUsersViewModel.class)) {
            return (T) new AdminUsersViewModel(userRepository);
        }

        if (modelClass.isAssignableFrom(ManageUserViewModel.class)) {
            return (T) new ManageUserViewModel(userRepository, userRoleRepository,
                    categoryRepository, sessionRepository);
        }

        if (modelClass.isAssignableFrom(AdminCategoriesViewModel.class)) {
            return (T) new AdminCategoriesViewModel(categoryRepository);
        }

        if (modelClass.isAssignableFrom(ManageCategoryViewModel.class)) {
            return (T) new ManageCategoryViewModel(categoryRepository, fileProviderService);
        }

        if (modelClass.isAssignableFrom(AdminProductsViewModel.class)) {
            return (T) new AdminProductsViewModel(productRepository);
        }

        if (modelClass.isAssignableFrom(ManageProductViewModel.class)) {
            return (T) new ManageProductViewModel(productRepository, categoryRepository,
                    sessionRepository, fileProviderService);
        }

        if (modelClass.isAssignableFrom(ShopProductsViewModel.class)) {
            return (T) new ShopProductsViewModel(productRepository, sessionRepository);
        }

        if (modelClass.isAssignableFrom(SearchProductViewModel.class)) {
            return (T) new SearchProductViewModel(productRepository, categoryRepository);
        }

        if (modelClass.isAssignableFrom(FavouriteProductsViewModel.class)) {
            return (T) new FavouriteProductsViewModel(preferencesRepository, sessionRepository);
        }

        if (modelClass.isAssignableFrom(ProductDetailViewModel.class)) {
            return (T) new ProductDetailViewModel(productRepository, userRepository,
                    cartRepository, preferencesRepository, sessionRepository);
        }

        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(productRepository, categoryRepository,
                    sessionRepository, preferencesRepository);
        }

        if (modelClass.isAssignableFrom(AccountViewModel.class)) {
            return (T) new AccountViewModel(sessionRepository);
        }

        if (modelClass.isAssignableFrom(PaymentViewModel.class)) {
            return (T) new PaymentViewModel(cartRepository, orderRepository, paymentRepository,
                    sessionRepository);
        }

        if (modelClass.isAssignableFrom(CartViewModel.class)) {
            return (T) new CartViewModel(cartRepository, sessionRepository);
        }

        if (modelClass.isAssignableFrom(PurchaseOrdersViewModel.class)) {
            return (T) new PurchaseOrdersViewModel(orderRepository, sessionRepository);
        }

        if (modelClass.isAssignableFrom(PurchaseOrderDetailViewModel.class)) {
            return (T) new PurchaseOrderDetailViewModel(orderRepository, userRepository);
        }

        if (modelClass.isAssignableFrom(SaleOrdersViewModel.class)) {
            return (T) new SaleOrdersViewModel(orderRepository, sessionRepository);
        }

        if (modelClass.isAssignableFrom(SaleOrderDetailViewModel.class)) {
            return (T) new SaleOrderDetailViewModel(orderRepository, userRepository, orderStatusRepository);
        }

        if (modelClass.isAssignableFrom(ManageReviewViewModel.class)) {
            return (T) new ManageReviewViewModel(reviewRepository, orderRepository, productRepository,
                    sessionRepository);
        }

        if (modelClass.isAssignableFrom(ProductReviewsViewModel.class)) {
            return (T) new ProductReviewsViewModel(reviewRepository);
        }

        if (modelClass.isAssignableFrom(UserReviewsViewModel.class)) {
            return (T) new UserReviewsViewModel(reviewRepository, sessionRepository);
        }

        if (modelClass.isAssignableFrom(ReviewDetailViewModel.class)) {
            return (T) new ReviewDetailViewModel(reviewRepository, orderRepository, productRepository);
        }

        if (modelClass.isAssignableFrom(BackupViewModel.class)) {
            return (T) new BackupViewModel(backupRepository);
        }

        if (modelClass.isAssignableFrom(BackupDetailViewModel.class)) {
            return (T) new BackupDetailViewModel(backupRepository);
        }

        if (modelClass.isAssignableFrom(UserMessagesViewModel.class)) {
            return (T) new UserMessagesViewModel(messageRepository, sessionRepository);
        }

        if (modelClass.isAssignableFrom(MessageDetailViewModel.class)) {
            return (T) new MessageDetailViewModel(messageRepository);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

package org.delcom.module

import org.delcom.repositories.*
import org.delcom.services.AuthService
import org.delcom.services.UserService
import org.delcom.services.CategoryService
import org.delcom.services.ArticleService
import org.delcom.services.CommentService
import org.koin.dsl.module

fun appModule(jwtSecret: String) = module {

    // User Repository
    single<IUserRepository> {
        UserRepository()
    }

    // Refresh Token Repository
    single<IRefreshTokenRepository> {
        RefreshTokenRepository()
    }

    // Category Repository
    single<ICategoryRepository> {
        CategoryRepository()
    }

    // Article Repository
    single<IArticleRepository> {
        ArticleRepository()
    }

    // Comment Repository
    single<ICommentRepository> {
        CommentRepository()
    }

    // Auth Service
    single {
        AuthService(jwtSecret, get(), get())
    }

    // User Service
    single {
        UserService(get())
    }

    // Category Service
    single {
        CategoryService(get())
    }

    // Article Service
    single {
        ArticleService(get())
    }

    // Comment Service
    single {
        CommentService(get())
    }
}
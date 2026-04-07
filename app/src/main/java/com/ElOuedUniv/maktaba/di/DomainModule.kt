package com.ElOuedUniv.maktaba.di

import com.ElOuedUniv.maktaba.data.repository.BookRepository
import com.ElOuedUniv.maktaba.data.repository.CategoryRepository
import com.ElOuedUniv.maktaba.domain.usecase.AddBookUseCase
import com.ElOuedUniv.maktaba.domain.usecase.GetBooksUseCase
import com.ElOuedUniv.maktaba.domain.usecase.GetCategoriesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for providing DOMAIN layer dependencies (Use Cases).
 *
 * @Module tells Hilt that this class contributes to the dependency graph.
 * @InstallIn(SingletonComponent::class) means these dependencies live for
 * the entire lifetime of the application.
 *
 * This module provides USE CASES:
 * - Each Use Case receives its required Repository via parameter injection.
 * - Hilt automatically resolves the Repository parameter using DataModule.
 *
 * Example chain: ViewModel → UseCase → Repository
 * Hilt builds this chain automatically!
 */
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    /**
     * Provides the GetBooksUseCase.
     *
     * Notice how [bookRepository] is passed as a parameter — Hilt automatically
     * provides this because DataModule already knows how to create a BookRepository.
     *
     * This replaces: val getBooksUseCase = GetBooksUseCase(bookRepository)
     *
     * @param bookRepository Automatically injected by Hilt from DataModule
     * @return A new instance of GetBooksUseCase
     */
    @Provides
    @Singleton
    fun provideGetBooksUseCase(bookRepository: BookRepository): GetBooksUseCase {
        return GetBooksUseCase(bookRepository)
    }

    /**
     * Provides the AddBookUseCase.
     *
     * @param bookRepository Automatically injected by Hilt from DataModule
     * @return A new instance of AddBookUseCase
     */
    @Provides
    @Singleton
    fun provideAddBookUseCase(bookRepository: BookRepository): AddBookUseCase {
        return AddBookUseCase(bookRepository)
    }

    /**
     * Provides the GetCategoriesUseCase.
     *
     * @param categoryRepository Automatically injected by Hilt from DataModule
     * @return A new instance of GetCategoriesUseCase
     */
    @Provides
    @Singleton
    fun provideGetCategoriesUseCase(categoryRepository: CategoryRepository): GetCategoriesUseCase {
        return GetCategoriesUseCase(categoryRepository)
    }
}

package com.ElOuedUniv.maktaba.di

import com.ElOuedUniv.maktaba.data.repository.BookRepository
import com.ElOuedUniv.maktaba.data.repository.BookRepositoryImpl
import com.ElOuedUniv.maktaba.data.repository.CategoryRepository
import com.ElOuedUniv.maktaba.data.repository.CategoryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for providing DATA layer dependencies.
 *
 * @Module tells Hilt that this class contributes to the dependency graph.
 * @InstallIn(SingletonComponent::class) means these dependencies live for
 * the entire lifetime of the application (singleton scope).
 *
 * This module provides REPOSITORY IMPLEMENTATIONS:
 * - When something asks for BookRepository → Hilt provides BookRepositoryImpl
 * - When something asks for CategoryRepository → Hilt provides CategoryRepositoryImpl
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    /**
     * Provides the BookRepository implementation.
     *
     * @Provides tells Hilt HOW to create an instance of BookRepository.
     * @Singleton ensures only ONE instance is created and reused everywhere.
     *
     * This replaces the manual: val bookRepository = BookRepositoryImpl()
     * that we had in MainActivity.
     *
     * @return A singleton instance of BookRepositoryImpl as BookRepository
     */
    @Provides
    @Singleton
    fun provideBookRepository(): BookRepository {
        return BookRepositoryImpl()
    }

    /**
     * Provides the CategoryRepository implementation.
     *
     * @Provides tells Hilt HOW to create an instance of CategoryRepository.
     * @Singleton ensures only ONE instance is created and reused everywhere.
     *
     * @return A singleton instance of CategoryRepositoryImpl as CategoryRepository
     */
    @Provides
    @Singleton
    fun provideCategoryRepository(): CategoryRepository {
        return CategoryRepositoryImpl()
    }
}

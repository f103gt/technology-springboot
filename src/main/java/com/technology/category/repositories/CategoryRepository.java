    package com.technology.category.repositories;

    import com.technology.category.models.Category;
    import jakarta.persistence.criteria.CriteriaBuilder;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;

    import java.util.List;
    import java.util.Optional;
    import java.util.Set;

    public interface CategoryRepository extends JpaRepository<Category,Integer> {
        @Query("select c from Category c where c.categoryName = :categoryName")
        Optional<Category> findCategoryByCategoryName(@Param("categoryName")String categoryName);
        void deleteCategoryByCategoryName(String categoryName);

        @Query("""
                select child from Category child left join
                child.parentCategory parent where parent.id = :parentId
                """)
        Set<Category> findAllChildCategoriesByParentCategoryId(Integer parentId);
    }

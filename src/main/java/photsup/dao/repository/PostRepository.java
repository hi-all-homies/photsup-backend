package photsup.dao.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import photsup.model.entity.Post;

import java.util.List;
import java.util.Optional;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "select p.postId from Post p")
    List<Long> findPostIds(Pageable pageable);

    @Query(value = "select distinct p from Post p " +
            "left join fetch p.author " +
            "left join fetch p.comments " +
            "left join fetch p.likes where p.postId in (:ids)")
    List<Post> findPostsByIds(@Param("ids") List<Long> ids);

    @Query(value = "from Post p left join fetch p.comments c " +
            "left join fetch c.author " +
            "left join fetch p.author " +
            "left join fetch p.likes where p.postId =:postId")
    Optional<Post> findPostById(@Param("postId") Long postId);

    @Modifying
    @Query(value = "update Post p set p.content =:content, p.imageUrl =:imageUrl " +
            "where p.postId =:postId")
    int updatePost(@Param("postId") Long postId,
                   @Param("content") String content,
                   @Param("imageUrl") String imageUrl);

    @Query(value = "select count(pl.user_id) from post_likes pl where " +
            "pl.post_id in(select p.post_id from post p where p.user_id =:userId)",
            nativeQuery = true)
    long countLikes(@Param("userId") Long userId);
}
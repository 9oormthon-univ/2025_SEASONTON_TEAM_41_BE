package com.debt.debt.domain.mypage.service;

import com.debt.debt.domain.community.dto.article.ArticleIndexResponseDto;
import com.debt.debt.domain.community.entity.Article;
import com.debt.debt.domain.community.entity.DebtType;
import com.debt.debt.domain.community.entity.Like;
import com.debt.debt.domain.community.repository.ArticleRepository;
import com.debt.debt.domain.community.repository.CommentRepository;
import com.debt.debt.domain.community.repository.LikeRepository;
import com.debt.debt.domain.mypage.dto.MyPageEditRequestDto;
import com.debt.debt.domain.mypage.dto.MyPageEditResponseDto;
import com.debt.debt.domain.mypage.dto.MyPageResponseDto;
import com.debt.debt.domain.user.entity.User;
import com.debt.debt.domain.user.repository.UserRepository;
import com.debt.debt.global.exception.CustomException;
import com.debt.debt.global.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikeRepository likeRepository;

    private final String uploadDir = "uploads/";

    public MyPageResponseDto show(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new MyPageResponseDto(user.getNickname(),user.getEmail(),user.getProfileImagePath());
    }

    @Transactional
    public MyPageEditResponseDto update(Long id, MyPageEditRequestDto requestDto) {
        User target = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        target.setNickname(requestDto.getNickname());
//        target.setDebtType(requestDto.getDebtType());
//        target.setDebtAmount(requestDto.getDebtAmount());

        MultipartFile file = requestDto.getProfileImage();
        if (file != null && !file.isEmpty()) {
            String uploadDir = "uploads/";
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);

            try {
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("파일 저장 실패");
            }

            target.setProfileImagePath("/uploads/" + fileName);
        }

        userRepository.save(target);
        return new MyPageEditResponseDto("수정이 완료되었습니다.");
    }


    public List<ArticleIndexResponseDto> index(String type, Long userId) {
        List<Article> articles;

        List<Like> likes = likeRepository.findByUserId(userId);

        if("MY".equalsIgnoreCase(type))
            articles = articleRepository.findByUserId(userId);
        else if("LIKE".equalsIgnoreCase(type))
            articles = likes.stream()
                    .map(Like::getArticle)
                    .toList();
        else throw new CustomException(ErrorCode.INVALID_ACTIVITY_TYPE);


        return articles.stream()
                .map(a -> new ArticleIndexResponseDto(
                        a.getId(),
                        a.getTitle(),
                        a.getContent(),
                        a.getDebtType(),
                        a.getUser().getNickname(),
                        a.getUser().getProfileImagePath(),
                        a.getCreatedAt(),
                        a.getLikes(),
                        commentRepository.countByArticleId(a.getId())
                ))
                .collect(Collectors.toList());
    }
}
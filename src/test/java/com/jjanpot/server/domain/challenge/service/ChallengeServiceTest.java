package com.jjanpot.server.domain.challenge.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jjanpot.server.domain.category.entity.Category;
import com.jjanpot.server.domain.category.entity.CategoryName;
import com.jjanpot.server.domain.category.repository.CategoryRepository;
import com.jjanpot.server.domain.challenge.dto.ChallengeCategoryRequest;
import com.jjanpot.server.domain.challenge.dto.CreateChallengeRequest;
import com.jjanpot.server.domain.challenge.dto.CreateChallengeResponse;
import com.jjanpot.server.domain.challenge.entity.Challenge;
import com.jjanpot.server.domain.challenge.entity.ChallengeCategory;
import com.jjanpot.server.domain.challenge.entity.ChallengeMinGoalPolicy;
import com.jjanpot.server.domain.challenge.entity.ChallengeStatus;
import com.jjanpot.server.domain.challenge.entity.ChallengeWeek;
import com.jjanpot.server.domain.challenge.repository.ChallengeCategoryRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeMinGoalPolicyRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeRepository;
import com.jjanpot.server.domain.challenge.repository.ChallengeWeekRepository;
import com.jjanpot.server.domain.team.entity.Team;
import com.jjanpot.server.domain.team.entity.TeamMembers;
import com.jjanpot.server.domain.team.entity.TeamType;
import com.jjanpot.server.domain.team.repository.TeamMembersRepository;
import com.jjanpot.server.domain.team.repository.TeamRepository;
import com.jjanpot.server.domain.user.entity.Provider;
import com.jjanpot.server.domain.user.entity.User;
import com.jjanpot.server.global.exception.BusinessException;
import com.jjanpot.server.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChallengeService 단위 테스트")
class ChallengeServiceTest {

	@Mock
	private TeamRepository teamRepository;
	@Mock
	private TeamMembersRepository teamMembersRepository;
	@Mock
	private ChallengeRepository challengeRepository;
	@Mock
	private ChallengeWeekRepository challengeWeekRepository;
	@Mock
	private ChallengeCategoryRepository challengeCategoryRepository;
	@Mock
	private ChallengeMinGoalPolicyRepository challengeMinGoalPolicyRepository;
	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private ChallengeService challengeService;

	private User user;
	private Category category1;
	private Category category2;
	private ChallengeMinGoalPolicy policy;
	private Team savedTeam;
	private Challenge savedChallenge;

	@BeforeEach
	void setUp() {
		user = User.create(Provider.KAKAO, "kakao_123", "테스터", "test@example.com", null);

		category1 = Category.builder()
			.categoryId(1L)
			.name(CategoryName.FOOD_DELIVERY)
			.defaultAmount(50000L)
			.sortOrder(1)
			.build();

		category2 = Category.builder()
			.categoryId(2L)
			.name(CategoryName.CAFE_DESSERT)
			.defaultAmount(30000L)
			.sortOrder(2)
			.build();

		// memberCount=4일 때 최소 목표 금액 정책: 100,000원
		policy = ChallengeMinGoalPolicy.builder()
			.policyId(1L)
			.memberCount(4)
			.minAmount(100_000)
			.build();

		savedTeam = Team.builder()
			.teamId(1L)
			.teamName("절약왕팀")
			.inviteCode("AB3K9P")
			.type(TeamType.FRIEND)
			.maxMemberCount(4)
			.build();

		savedChallenge = Challenge.builder()
			.challengeId(1L)
			.title("절약왕팀")
			.goalAmount(200_000)
			.minPersonalGoalAmount(30_000)
			.status(ChallengeStatus.WAITING)
			.startDate(LocalDate.of(2026, 3, 25).atStartOfDay())
			.endDate(LocalDate.of(2026, 3, 25).atStartOfDay().plusWeeks(1))
			.team(savedTeam)
			.build();
	}

	@Nested
	@DisplayName("챌린지 생성")
	class CreateChallenge {

		@Test
		@DisplayName("카테고리 1개 - 성공")
		void 카테고리_1개_성공() {
			// given
			CreateChallengeRequest request = createRequest(
				List.of(new ChallengeCategoryRequest(1L, 50_000))
			);

			mockSuccessScenario(List.of(category1), List.of(
				ChallengeCategory.of(savedChallenge, category1, 50_000)
			));

			// when
			CreateChallengeResponse response = challengeService.createChallenge(user, request);

			// then
			assertThat(response.challengeId()).isEqualTo(1L);
			assertThat(response.teamName()).isEqualTo("절약왕팀");
			assertThat(response.inviteCode()).isEqualTo("AB3K9P");
			assertThat(response.goalAmount()).isEqualTo(200_000);
			assertThat(response.categories()).hasSize(1);
			assertThat(response.categories().get(0).categoryId()).isEqualTo(1);
		}

		@Test
		@DisplayName("카테고리 3개 (최대) - 성공")
		void 카테고리_3개_성공() {
			// given
			Category category3 = Category.builder()
				.categoryId(3L)
				.name(CategoryName.TRANSPORT)
				.defaultAmount(20_000L)
				.sortOrder(3)
				.build();

			CreateChallengeRequest request = createRequest(List.of(
				new ChallengeCategoryRequest(1L, 50_000),
				new ChallengeCategoryRequest(2L, 30_000),
				new ChallengeCategoryRequest(3L, 20_000)
			));

			mockSuccessScenario(List.of(category1, category2, category3), List.of(
				ChallengeCategory.of(savedChallenge, category1, 50_000),
				ChallengeCategory.of(savedChallenge, category2, 30_000),
				ChallengeCategory.of(savedChallenge, category3, 20_000)
			));

			// when
			CreateChallengeResponse response = challengeService.createChallenge(user, request);

			// then
			assertThat(response.categories()).hasSize(3);
		}

		@Test
		@DisplayName("초대코드 중복 시 재생성 후 성공")
		void 초대코드_중복_재생성_성공() {
			// given
			CreateChallengeRequest request = createRequest(
				List.of(new ChallengeCategoryRequest(1L, 50_000))
			);

			when(challengeMinGoalPolicyRepository.findByMemberCount(4)).thenReturn(Optional.of(policy));
			// 처음 두 번은 중복, 세 번째는 unique
			when(teamRepository.existsByInviteCode(anyString()))
				.thenReturn(true)
				.thenReturn(true)
				.thenReturn(false);
			when(teamRepository.save(any(Team.class))).thenReturn(savedTeam);
			when(teamMembersRepository.save(any(TeamMembers.class))).thenReturn(null);
			when(challengeRepository.save(any(Challenge.class))).thenReturn(savedChallenge);
			when(challengeWeekRepository.save(any(ChallengeWeek.class))).thenReturn(null);
			when(categoryRepository.findAllById(anyList())).thenReturn(List.of(category1));
			when(challengeCategoryRepository.saveAll(anyList())).thenReturn(
				List.of(ChallengeCategory.of(savedChallenge, category1, 50_000))
			);

			// when
			challengeService.createChallenge(user, request);

			// then: existsByInviteCode 가 3번 호출되었는지 확인
			verify(teamRepository, times(3)).existsByInviteCode(anyString());
		}

		@Test
		@DisplayName("팀, 팀원, 챌린지, 챌린지주차, 챌린지카테고리 저장 순서 검증")
		void 저장_호출_검증() {
			// given
			CreateChallengeRequest request = createRequest(
				List.of(new ChallengeCategoryRequest(1L, 50_000))
			);
			mockSuccessScenario(List.of(category1), List.of(
				ChallengeCategory.of(savedChallenge, category1, 50_000)
			));

			// when
			challengeService.createChallenge(user, request);

			// then
			verify(teamRepository, times(1)).save(any(Team.class));
			verify(teamMembersRepository, times(1)).save(any(TeamMembers.class));
			verify(challengeRepository, times(1)).save(any(Challenge.class));
			verify(challengeWeekRepository, times(1)).save(any(ChallengeWeek.class));
			verify(challengeCategoryRepository, times(1)).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - 인원수에 해당하는 최소 목표 금액 정책 없음")
		void 실패_정책없음() {
			// given
			CreateChallengeRequest request = createRequest(
				List.of(new ChallengeCategoryRequest(1L, 50_000))
			);
			when(challengeMinGoalPolicyRepository.findByMemberCount(4)).thenReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> challengeService.createChallenge(user, request))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ErrorCode.CHALLENGE_MIN_GOAL_POLICY_NOT_FOUND);

			verify(teamRepository, never()).save(any());
		}

		@Test
		@DisplayName("실패 - 팀 전체 목표 금액이 최소 기준 미달")
		void 실패_목표금액_최소기준미달() {
			// given: 정책 최소금액 500,000원, 요청 금액 200,000원
			ChallengeMinGoalPolicy strictPolicy = ChallengeMinGoalPolicy.builder()
				.policyId(1L)
				.memberCount(4)
				.minAmount(500_000)
				.build();

			CreateChallengeRequest request = createRequest(
				List.of(new ChallengeCategoryRequest(1L, 50_000))
			);
			when(challengeMinGoalPolicyRepository.findByMemberCount(4)).thenReturn(Optional.of(strictPolicy));

			// when & then
			assertThatThrownBy(() -> challengeService.createChallenge(user, request))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ErrorCode.GOAL_AMOUNT_BELOW_MINIMUM);

			verify(teamRepository, never()).save(any());
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 카테고리 ID")
		void 실패_카테고리없음() {
			// given
			CreateChallengeRequest request = createRequest(
				List.of(new ChallengeCategoryRequest(999L, 50_000)) // 존재하지 않는 ID
			);

			when(challengeMinGoalPolicyRepository.findByMemberCount(4)).thenReturn(Optional.of(policy));
			when(teamRepository.existsByInviteCode(anyString())).thenReturn(false);
			when(teamRepository.save(any(Team.class))).thenReturn(savedTeam);
			when(teamMembersRepository.save(any(TeamMembers.class))).thenReturn(null);
			when(challengeRepository.save(any(Challenge.class))).thenReturn(savedChallenge);
			when(challengeWeekRepository.save(any(ChallengeWeek.class))).thenReturn(null);
			when(categoryRepository.findAllById(anyList())).thenReturn(List.of()); // 비어있음

			// when & then
			assertThatThrownBy(() -> challengeService.createChallenge(user, request))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);

			verify(challengeCategoryRepository, never()).saveAll(anyList());
		}

		// 공통 요청 생성 헬퍼 (memberCount=4, goalAmount=200,000, startDate=2026-03-25)
		private CreateChallengeRequest createRequest(List<ChallengeCategoryRequest> categories) {
			return new CreateChallengeRequest(
				"절약왕팀",
				TeamType.FRIEND,
				4,
				LocalDate.of(2026, 3, 25),
				categories,
				200_000,
				30_000
			);
		}

		// 성공 시나리오 공통 mock 설정 헬퍼
		private void mockSuccessScenario(List<Category> categories, List<ChallengeCategory> savedCategories) {
			when(challengeMinGoalPolicyRepository.findByMemberCount(4)).thenReturn(Optional.of(policy));
			when(teamRepository.existsByInviteCode(anyString())).thenReturn(false);
			when(teamRepository.save(any(Team.class))).thenReturn(savedTeam);
			when(teamMembersRepository.save(any(TeamMembers.class))).thenReturn(null);
			when(challengeRepository.save(any(Challenge.class))).thenReturn(savedChallenge);
			when(challengeWeekRepository.save(any(ChallengeWeek.class))).thenReturn(null);
			when(categoryRepository.findAllById(anyList())).thenReturn(categories);
			when(challengeCategoryRepository.saveAll(anyList())).thenReturn(savedCategories);
		}
	}
}

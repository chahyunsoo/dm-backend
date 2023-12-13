package com.DM.DeveloperMatching.service;

import com.DM.DeveloperMatching.domain.*;
import com.DM.DeveloperMatching.repository.ArticleRepository;
import com.DM.DeveloperMatching.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.DM.DeveloperMatching.domain.Level.MASTER;
import static com.DM.DeveloperMatching.domain.Level.SENIOR;
import static java.lang.Double.NaN;

@RequiredArgsConstructor
@Service
@Transactional
public class RecommendService {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    public List<Article> recommendProjectByCS(Long uId) {
        User user = userRepository.findById(uId).
                orElseThrow(EntityNotFoundException::new);
        List<Article> projects = articleRepository.findAll();
        projects = filterRec(projects, user);
        Map<Article, Double> smap = new HashMap<>();
        boolean hasOnlyManagers = user.getUserInMember().stream()
                .allMatch(member -> member.getMemberStatus() == MemberStatus.MANAGER);

        if(!user.getUserInMember().isEmpty() && !hasOnlyManagers) {
            for(Article p : projects) {
                smap.put(p, cosineSimilarity(user, p) - euclideanDistance(user, p));
            }
        } else {
            for (Article p : projects) {
                smap.put(p, cosineSimilarity(user, p));
            }
        }
        List<Map.Entry<Article, Double>> entryList = new ArrayList<Map.Entry<Article, Double>>(smap.entrySet());
        Collections.sort(entryList, (obj1, obj2) -> obj2.getValue().compareTo(obj1.getValue()));
        List<Article> result = new ArrayList<>();
        System.out.println(entryList);

        for(Map.Entry<Article, Double> entry : entryList) {
            result.add(entry.getKey());
        }

        return result;
    }

    public List<Article> recommendProjectByCS(Long uId, List<String> recPart, List<String> recTech, List<String> recLevel) {
        User user = userRepository.findById(uId).
                orElseThrow(EntityNotFoundException::new);
        List<Article> projects = new ArrayList<>();

        if(!(recPart.isEmpty()) && recTech.isEmpty() && recLevel.isEmpty()) {
            String queryString = "SELECT a FROM Article a WHERE " + makePartQuery(recPart);
            Query query = entityManager.createQuery(queryString, Article.class);
            projects = query.getResultList();
            projects = filterRec(projects, user);
        }
        else if(recPart.isEmpty() && !(recTech.isEmpty()) && recLevel.isEmpty()) {
            String queryString = makeTechQuery(recTech);
            Query query = entityManager.createQuery(queryString, Article.class);
            projects = query.getResultList();
            projects = filterRec(projects, user);
        }
        else if(recPart.isEmpty() && recTech.isEmpty() && !(recLevel.isEmpty())) {
            List<Level> recLevels = recLevel.stream()
                    .map(String::toUpperCase)
                    .map(Level::valueOf)
                    .collect(Collectors.toList());
            projects = articleRepository.findAllByRecLevel(recLevels);
            projects = filterRec(projects, user);
        }
        else if(!(recPart.isEmpty()) && !(recTech.isEmpty()) && recLevel.isEmpty()) {
            String queryString = makeTechQuery(recTech) + "AND (" + makePartQuery(recPart) + ")";
            Query query = entityManager.createQuery(queryString, Article.class);
            projects = query.getResultList();
            projects = filterRec(projects, user);
        }
        else if(!(recPart.isEmpty()) && recTech.isEmpty() && !(recLevel.isEmpty())) {
            String queryString = "SELECT a FROM Article a WHERE " + makePartQuery(recPart) + makeLevelQuery(recLevel);
            Query query = entityManager.createQuery(queryString, Article.class);
            projects = query.getResultList();
            projects = filterRec(projects, user);
        }
        else if(recPart.isEmpty() && !(recTech.isEmpty()) && !(recLevel.isEmpty())) {
            String queryString = makeTechQuery(recTech) + makeLevelQuery(recLevel);
            Query query = entityManager.createQuery(queryString, Article.class);
            projects = query.getResultList();
            projects = filterRec(projects, user);
        }
        else {
            String queryString = makeTechQuery(recTech) + "AND (" + makePartQuery(recPart) + ")" + makeLevelQuery(recLevel);
            Query query = entityManager.createQuery(queryString, Article.class);
            projects = query.getResultList();
            projects = filterRec(projects, user);
        }
        Map<Article, Double> smap = new HashMap<>();
        boolean hasOnlyManagers = user.getUserInMember().stream()
                .allMatch(member -> member.getMemberStatus() == MemberStatus.MANAGER);
        if(!user.getUserInMember().isEmpty() && !hasOnlyManagers) {
            for(Article p : projects) {
                smap.put(p, cosineSimilarity(user, p) - euclideanDistance(user, p));
            }
        } else {
            for (Article p : projects) {
                smap.put(p, cosineSimilarity(user, p));
            }
        }
        List<Map.Entry<Article, Double>> entryList = new ArrayList<Map.Entry<Article, Double>>(smap.entrySet());
        Collections.sort(entryList, (obj1, obj2) -> obj2.getValue().compareTo(obj1.getValue()));
        List<Article> result = new ArrayList<>();
        for(Map.Entry<Article, Double> entry : entryList) {
            result.add(entry.getKey());
        }

        return result;
    }

    public List<List<User>> recommendUserByCS(Long aId) {
        Article article = articleRepository.findById(aId)
                .orElseThrow(EntityNotFoundException::new);
        List<String> parts = Arrays.asList(article.getRecPart().split(", \\s*"));
        for(String s: parts) {
            System.out.println(s);
        }
        List<List<User>> temp = new ArrayList<>();

        for(String p : parts) {
            temp.add(userRepository.findAllByPart(p).stream()
                    .filter(sub -> sub != article.getArticleOwner())
                    .collect(Collectors.toList()));
        }
        List<List<User>> result = new ArrayList<>();
        for(List<User> users : temp) {
            Map<User, Double> tempMap = new HashMap<>();
            for (User user : users) {
                tempMap.put(user, cosineSimilarity(user, article));
            }

            List<User> keySet = new ArrayList<>(tempMap.keySet());

            keySet.sort((o1, o2) -> tempMap.get(o2).compareTo(tempMap.get(o1)));
            result.add(keySet);
        }

        return result;
    }

    private List<Article> filterRec(List<Article> result, User user) {
        if(!user.getUserInMember().isEmpty()) {
            result = result.stream()
                    .filter(article ->
                            user.getUserInMember().stream()
                                    .noneMatch(member ->
                                            member.getProject().getArticle().getAId() == article.getAId() &&
                                                    (member.getMemberStatus() == MemberStatus.MANAGER || member.getMemberStatus() == MemberStatus.WAITING)))
                    .collect(Collectors.toList());
        }

        return result.stream()
                .filter(sub -> sub.getProject().getProjectStatus() == ProjectStatus.RECRUITING)
                .filter(sub -> sub.getArticleOwner() != user)
                .collect(Collectors.toList());
    }

    private String makeTechQuery(List<String> recTech) {
        String queryString = "SELECT a FROM Article a WHERE " +
                recTech.stream()
                        .map(tech -> "a.recTech LIKE '%" + tech + "%'")
                        .collect(Collectors.joining(" AND "));

        return queryString;
    }

    private String makePartQuery(List<String> recPart) {
        String inQueries = recPart.stream()
                .map(part -> "a.recPart LIKE '%" + part + "%'")
                .collect(Collectors.joining(" OR "));

        return inQueries;
    }

    private String makeLevelQuery(List<String> recLevel) {
        String inQueries = recLevel.stream()
                .map(part -> "'" + part.replace("'", "''") + "'") // 문자열은 싱글 쿼트로 감싸야 함
                .collect(Collectors.joining(", "));

        return " AND a.recLevel IN (" + inQueries + ")";
    }

    public List<Integer> vectorP(String part) {
        final String[] parts = {"BackEnd", "FrontEnd", "Mobile", "Design"};
        Map<String, Integer> vectors = new HashMap<>();
        for (String p : parts) {
            vectors.put(p, part.contains(p) ? 1 : 0);
        }
        return new ArrayList<Integer>(vectors.values());
    }

    public List<Integer> vectorL(Level level) {
        if(level == MASTER) {
            return new ArrayList<>(Arrays.asList(1, 0, 0));
        } else if(level == SENIOR) {
            return new ArrayList<>(Arrays.asList(0, 1, 0));
        } else {
            return new ArrayList<>(Arrays.asList(0, 0, 1));
        }
    }

    public List<Integer> vectorTechBack(String tech) {
        final String[] techs = {"java", "Spring", "Nest.js", "Node.js","Go", "Kotlin", "Express", "MySQL", "MongoDB",
                "Python", "Django", "PHP", "GraphQL", "AWS", "Kubernetes", "Docker", "Firebase", "C언어"};
        Map<String, Integer> vectors = new HashMap<>();
        for (String t : techs) {
            vectors.put(t, tech.contains(t) ? 1 : 0);
        }
        return new ArrayList<Integer>(vectors.values());
    }

    public List<Integer> vectorTechFront(String tech) {
        final String[] techs = {"react", "Vue.js", "JavaScript", "TypeScript", "Svelte", "Next.js", "Jest"};
        Map<String, Integer> vectors = new HashMap<>();
        for (String t : techs) {
            vectors.put(t, tech.contains(t) ? 1 : 0);
        }
        return new ArrayList<Integer>(vectors.values());
    }

    public List<Integer> vectorTechMobile(String tech) {
        final String[] techs = {"Flutter", "Swift", "Kotlin", "ReactiveNative", "Unity"};
        Map<String, Integer> vectors = new HashMap<>();
        for (String t : techs) {
            vectors.put(t, tech.contains(t) ? 1 : 0);
        }
        return new ArrayList<Integer>(vectors.values());
    }

    public List<Integer> vectorTechDesign(String tech) {
        final String[] techs = {"Figma", "Zeplin"};
        Map<String, Integer> vectors = new HashMap<>();
        for (String t : techs) {
            vectors.put(t, tech.contains(t) ? 1 : 0);
        }
        return new ArrayList<Integer>(vectors.values());
    }

    public Double cosineSimilarity(User user, Article article) {
        Double result = 0.0;

        List<List<Integer>> userVector = new ArrayList<>();
        userVector.add(vectorP(user.getPart()));
        userVector.get(0).addAll(vectorL(user.getLevel()));
        userVector.add(vectorTechBack(user.getTech()));
        userVector.add(vectorTechFront(user.getTech()));
        userVector.add(vectorTechMobile(user.getTech()));
        userVector.add(vectorTechDesign(user.getTech()));
        List<List<Integer>> proVector = new ArrayList<>();
        proVector.add(vectorP(article.getRecPart()));
        proVector.get(0).addAll(vectorL(article.getRecLevel()));
        proVector.add(vectorTechBack(article.getRecTech()));
        proVector.add(vectorTechFront(article.getRecTech()));
        proVector.add(vectorTechMobile(article.getRecTech()));
        proVector.add(vectorTechDesign(article.getRecTech()));

        for(int i = 0; i < userVector.size(); i++) {
            if(user.getPart().equals("BackEnd") && i == 1) {
                if(!CS(userVector.get(i), proVector.get(i)).isNaN()) {
                    result += 1.3 * CS(userVector.get(i), proVector.get(i));
                }
            }
            if(user.getPart().equals("FrontEnd") && i == 2) {
                if(!CS(userVector.get(i), proVector.get(i)).isNaN()) {
                    result += 1.3 * CS(userVector.get(i), proVector.get(i));
                }
            }
            if(user.getPart().contains("Mobile") && i == 3) {
                if(!CS(userVector.get(i), proVector.get(i)).isNaN()) {
                    result += 1.3 * CS(userVector.get(i), proVector.get(i));
                }
            }
            if(user.getPart().equals("Design") && i == 4) {
                if(!CS(userVector.get(i), proVector.get(i)).isNaN()) {
                    result += 1.3 * CS(userVector.get(i), proVector.get(i));
                }
            }
            else {
                if(!CS(userVector.get(i), proVector.get(i)).isNaN()) {
                    result += CS(userVector.get(i), proVector.get(i));
                }
            }
        }
        return result;
    }

    public Double euclideanDistance(User user, Article article) {
        Double result = 0.0;
        List<Article> history = new ArrayList<>();
        for(Member m : user.getUserInMember()) {
            if(m.getMemberStatus() != MemberStatus.MANAGER) {
                history.add(m.getProject().getArticle());
            }
        }
        List<List<Double>> historyVector = new ArrayList<>();
        List<List<Double>> proVector = new ArrayList<>();
        List<List<Double>> temp1 = new ArrayList<>();
        List<List<Double>> temp2 = new ArrayList<>();
        List<List<Double>> temp3 = new ArrayList<>();

        for(int i = 0; i < history.size(); i++) {
            temp1.add(vectorP(history.get(i).getRecPart()).stream()
                    .map(integer -> integer.doubleValue())
                    .collect(Collectors.toList()));
            temp2.add(vectorL(history.get(i).getRecLevel()).stream()
                    .map(integer -> integer.doubleValue())
                    .collect(Collectors.toList()));
        }
        proVector.add(vectorP(article.getRecPart()).stream()
                .map(integer -> integer.doubleValue())
                .collect(Collectors.toList()));
        proVector.get(0).addAll(vectorL(article.getRecLevel()).stream()
                .map(integer -> integer.doubleValue())
                .collect(Collectors.toList()));

        if(user.getPart().equals("BackEnd")) {
            for (int i = 0; i < history.size(); i++) {
                temp3.add(vectorTechBack(history.get(i).getRecTech()).stream()
                        .map(integer -> integer.doubleValue())
                        .collect(Collectors.toList()));
            }
            proVector.add(vectorTechBack(article.getRecTech()).stream()
                    .map(integer -> integer.doubleValue())
                    .collect(Collectors.toList()));
        }
        else if(user.getPart().equals("FrontEnd")) {
            for (int i = 0; i < history.size(); i++) {
                temp3.add(vectorTechFront(history.get(i).getRecTech()).stream()
                        .map(integer -> integer.doubleValue())
                        .collect(Collectors.toList()));
            }
            proVector.add(vectorTechFront(article.getRecTech()).stream()
                    .map(integer -> integer.doubleValue())
                    .collect(Collectors.toList()));
        }
        else if(user.getPart().contains("Mobile")) {
            for (int i = 0; i < history.size(); i++) {
                temp3.add(vectorTechMobile(history.get(i).getRecTech()).stream()
                        .map(integer -> integer.doubleValue())
                        .collect(Collectors.toList()));
            }
            proVector.add(vectorTechMobile(article.getRecTech()).stream()
                    .map(integer -> integer.doubleValue())
                    .collect(Collectors.toList()));
        }
        else if(user.getPart().equals("Design")) {
            for (int i = 0; i < history.size(); i++) {
                temp3.add(vectorTechDesign(history.get(i).getRecTech()).stream()
                        .map(integer -> integer.doubleValue())
                        .collect(Collectors.toList()));
            }
            proVector.add(vectorTechDesign(article.getRecTech()).stream()
                    .map(integer -> integer.doubleValue())
                    .collect(Collectors.toList()));
        }
        historyVector.add(sumList(temp1));
        historyVector.get(0).addAll(sumList(temp2));
        historyVector.add(sumList(temp3));
        System.out.println(proVector);
        System.out.println(historyVector);
        for(int i = 0; i < historyVector.size(); i++) {
            if(i == historyVector.size() - 1) {
                result += ED1(proVector.get(i), historyVector.get(i));
            }
            else {
                result += ED2(proVector.get(i), historyVector.get(i));
            }
        }
        //System.out.println(result);
        return result;
    }

    public Double CS(List<Integer> vectorA, List<Integer> vectorB) {
        Double dotProduct = 0.0;
        Double normA = 0.0;
        Double normB = 0.0;

        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }
        Double result = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        return result;
    }

    public Double ED1(List<Double> vectorA, List<Double> vectorB) {
        int dimensions = vectorA.size();
        double sum = 0.0;

        for (int i = 0; i < dimensions; i++) {
            if(vectorA.get(i) != 0.0 && vectorB.get(i) != 0.0) {
                double diff = vectorA.get(i) - vectorB.get(i);
                sum += Math.pow(diff, 2);
            }
            else if(vectorA.get(i) == 0.0 && vectorB.get(i) != 0.0) {
                sum += 0.7;
            }
            else if(vectorA.get(i) != 0.0 && vectorB.get(i) == 0.0) {
                sum += 0.01;
            }
        }
        System.out.println(Math.sqrt(sum));
        return Math.sqrt(sum);
    }

    public Double ED2(List<Double> vectorA, List<Double> vectorB) {
        int dimensions = vectorA.size();
        double sum = 0.0;

        for (int i = 0; i < dimensions; i++) {
            if(vectorA.get(i) != 0.0 && vectorB.get(i) != 0.0) {
                double diff = vectorA.get(i) - vectorB.get(i);
                sum += Math.pow(diff, 2);
            }
        }

        return Math.sqrt(sum);
    }

    public List<Double> sumList(List<List<Double>> input) {
        List<Double> value = new ArrayList<>();
        for(List<Double> list : input) {
            value = addLists(value, list);
        }
        List<Double> result = new ArrayList<>();
        for(int i = 0; i < value.size(); i++) {
            if((value.get(i) / input.size()) < 0.5 && (value.get(i) / input.size()) != 0.0) {
                result.add(1 - (value.get(i) / input.size()));
            }
            else {
                result.add(value.get(i) / input.size());
            }
        }
        return result;
    }

    private static List<Double> addLists(List<Double> list1, List<Double> list2) {
        List<Double> result = new ArrayList<>();
        if(list1.isEmpty()) {
            return list2;
        }
        int size = Math.min(list1.size(), list2.size());

        for (int i = 0; i < size; i++) {
            double sum = list1.get(i) + list2.get(i);
            result.add(sum);
        }

        return result;
    }
}
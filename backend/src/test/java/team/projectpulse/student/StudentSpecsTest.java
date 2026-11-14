package team.projectpulse.student;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentSpecsTest {
    @Mock
    private Root<Student> root;
    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    private Path<String> firstName;
    @Mock
    private Expression<String> lowerFirstName;
    @Mock
    private Predicate expectedPredicate;

    @Test
    @ResourceLock(Resources.LOCALE)
    void firstNameSearchUsesSamePatternUnderTurkishDefaultLocale() {
        when(root.<String>get("firstName")).thenReturn(firstName);
        when(criteriaBuilder.lower(firstName)).thenReturn(lowerFirstName);
        when(criteriaBuilder.like(lowerFirstName, "%ivan%")).thenReturn(expectedPredicate);

        Locale original = Locale.getDefault();
        Locale display = Locale.getDefault(Locale.Category.DISPLAY);
        Locale format = Locale.getDefault(Locale.Category.FORMAT);
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Predicate actual = StudentSpecs.containsFirstName("Ivan").toPredicate(root, null, criteriaBuilder);
            assertThat(actual).isSameAs(expectedPredicate);
        } finally {
            Locale.setDefault(original);
            Locale.setDefault(Locale.Category.DISPLAY, display);
            Locale.setDefault(Locale.Category.FORMAT, format);
        }
    }
}

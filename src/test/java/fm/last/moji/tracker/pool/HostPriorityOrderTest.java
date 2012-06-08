package fm.last.moji.tracker.pool;

import static fm.last.moji.tracker.pool.HostPriorityOrder.INSTANCE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HostPriorityOrderTest {

  private static final int LEFT_IS_PREFERED = 1;
  private static final int RIGHT_IS_PREFERED = -1;
  private static final int NO_PREFERENCE = 0;
  @Mock
  private ManagedTrackerHost mockManagedHost1;
  @Mock
  private ManagedTrackerHost mockManagedHost2;

  @Test
  public void identicalHostState() {
    when(mockManagedHost1.getLastFailed()).thenReturn(0L);
    when(mockManagedHost1.getLastUsed()).thenReturn(0L);
    when(mockManagedHost2.getLastFailed()).thenReturn(0L);
    when(mockManagedHost2.getLastUsed()).thenReturn(0L);

    int actual = INSTANCE.compare(mockManagedHost1, mockManagedHost2);
    assertThat(actual, is(NO_PREFERENCE));
  }

  @Test
  public void leftFailedMoreRecentlyFavourRight() {
    when(mockManagedHost1.getLastFailed()).thenReturn(1L);
    when(mockManagedHost1.getLastUsed()).thenReturn(0L);
    when(mockManagedHost2.getLastFailed()).thenReturn(0L);
    when(mockManagedHost2.getLastUsed()).thenReturn(0L);

    int actual = INSTANCE.compare(mockManagedHost1, mockManagedHost2);
    assertThat(actual, is(RIGHT_IS_PREFERED));
  }

  @Test
  public void rightFailedMoreRecentlyFavourLeft() {
    when(mockManagedHost1.getLastFailed()).thenReturn(0L);
    when(mockManagedHost1.getLastUsed()).thenReturn(0L);
    when(mockManagedHost2.getLastFailed()).thenReturn(1L);
    when(mockManagedHost2.getLastUsed()).thenReturn(0L);

    int actual = INSTANCE.compare(mockManagedHost1, mockManagedHost2);
    assertThat(actual, is(LEFT_IS_PREFERED));
  }

  @Test
  public void favoursLeastRecentlyUsedLeft() {
    when(mockManagedHost1.getLastFailed()).thenReturn(10L);
    when(mockManagedHost1.getLastUsed()).thenReturn(3L);
    when(mockManagedHost2.getLastFailed()).thenReturn(10L);
    when(mockManagedHost2.getLastUsed()).thenReturn(1L);

    int actual = INSTANCE.compare(mockManagedHost1, mockManagedHost2);
    assertThat(actual, is(RIGHT_IS_PREFERED));
  }

  @Test
  public void favoursLeastRecentlyUsedRight() {
    when(mockManagedHost1.getLastFailed()).thenReturn(10L);
    when(mockManagedHost1.getLastUsed()).thenReturn(1L);
    when(mockManagedHost2.getLastFailed()).thenReturn(10L);
    when(mockManagedHost2.getLastUsed()).thenReturn(3L);

    int actual = INSTANCE.compare(mockManagedHost1, mockManagedHost2);
    assertThat(actual, is(LEFT_IS_PREFERED));
  }

}
import { ref, computed } from 'vue'
export function usePagination(initial=1, size=10){
  const page = ref(initial); const pageSize = ref(size); const offset = computed(()=> (page.value-1)*pageSize.value)
  return { page, pageSize, offset }
}

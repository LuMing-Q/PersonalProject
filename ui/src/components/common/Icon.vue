<template>
	<svg aria-hidden="true" :style="{ fill: color }">
		<use :xlink:href="name.startsWith('icon-') ? `#${name}` : `#icon-${name}`" />
	</svg>
</template>

<script setup name="Icon">
defineProps({
	color: { type: String, default: 'black' },
	name: { type: String, required: true }
});
</script>

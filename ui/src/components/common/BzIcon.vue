<template>
	<svg aria-hidden="true" :style="{ fill: color, width: `${size}px`, height: `${size}px` }">
		<use :xlink:href="name.startsWith('icon-') ? `#${name}` : `#icon-${name}`" />
	</svg>
</template>

<script setup name="BzIcon">
defineProps({
	color: { type: String, default: 'black' },
	name: { type: String, required: true },
	size: { type: Number, default: 14 },
});
</script>
